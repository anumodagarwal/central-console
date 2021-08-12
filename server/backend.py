import logging
import subprocess
import json
import os
import time

logger = logging.getLogger(__name__)


houston_domain_account_map = {'alpha': ['ihm-houston-alpha-pdx'],
                              'beta': ['ihm-houston-test-pdx'],
                              'gamma': ['ihm-houston-gamma-pdx', 'ihm-houston-gamma-dub'],
                              'prod': ['ihm-houston-prod-asset-dub1', 'ihm-houston-prod-asset-iad1', 'ihm-houston-prod-drthy-iad1',
                                       'ihm-houston-prod-ihm-dub1', 'ihm-houston-prod-ihm-iad2', 'ihm-houston-prod-ihm-pdx3', 'ihm-houston-prod-mendel-iad1',
                                       'ihm-houston-prod-mendel-pdx1', 'ihm-houston-prod-pdx', 'ihm-houston-prod-pdx2']}
houston_account_id_map = {'ihm-houston-alpha-pdx': 324329000739, 'ihm-houston-test-pdx': 324329000739}


def run_cmd(cmd):
    fresult = ''
    process = subprocess.Popen(cmd.split(" "),
                               stdout=subprocess.PIPE,
                               universal_newlines=True)
    while True:
        output = process.stdout.readline()
        # print (output)
        fresult += output.strip()
        # Do something else
        return_code = process.poll()
        if return_code is not None:
            # Process has finished, read rest of the output
            for output in process.stdout.readlines():
                fresult += output.strip()
            break
    dic = json.loads(fresult)
    # print ("after")
    # print (dic)
    return dic


def domain_to_cell_mapping(servicename, domain):
    return houston_domain_account_map[domain]


def cellId_to_instances_mapping(cellId):
    instid_namelist = []
    # to have profile name same as cellId DO CHANGE THIS LATER
    # cellId = 'houstonalpharole'
    instnamecmd = 'aws ec2 describe-instances --instance-ids {} --profile {}'
    cmd = 'aws ssm describe-instance-information --profile {}'.format(cellId)
    instanceinformation = run_cmd(cmd)
    instanceinformation = instanceinformation["InstanceInformationList"]
    for inst in instanceinformation:
        instname = ""
        instId = inst["InstanceId"]
        temcmd = instnamecmd.format(instId, cellId)
        instdetailedinfo = run_cmd(temcmd)
        insttags = instdetailedinfo["Reservations"][0]["Instances"][0]["Tags"]
        for tag in insttags:
            if tag["Key"] == "Name":
                instname = tag["Value"]
                instid_namelist.append(instname + ' (ID:' + instId + ')')
    return instid_namelist


def run_commands(instidname, usercmd, cell):
    final_output = {}
    param = json.dumps({"commands": [usercmd]}) #.replace(" ", "")
    cmd = "aws --profile {} ssm send-command --instance-ids {} --document-name AWS-RunShellScript --output text " \
          "--parameters \'{}\' --query \"Command.CommandId\""
    outputcmd = "aws --profile %s ssm list-command-invocations --details --command-id %s --query \"CommandInvocations[].CommandPlugins[].{" \
                "Status:Status,Output:Output}\""
    for insta in instidname:
        intid = insta.split(':')[1].replace(")", "")
        tcmd = cmd.format(cell, intid, param)
        stream = os.popen(tcmd)
        output = stream.read()
        time.sleep(2)
        tcmd = outputcmd % (cell, output.strip())
        stream = os.popen(tcmd)
        output = stream.read()
        output = json.loads(output)
        final_output[insta] = output[0]  # assuming we are passing only 1 command # { "Status": "InProgress","Output": ""}
    return final_output


def run_command_on_instances(params):
    serivename = params["service_name"]
    domain = params["domain"]
    usercmd = params["command"]
    instanceinfo = params["instance_info"]
    final_output = {}
    for cell in instanceinfo.keys():
        instance_ids_in_cell = instanceinfo[cell]
        cell_inst_output = run_commands(instance_ids_in_cell, usercmd, cell)
        # print (cell_inst_output)
        temp1 = {}
        for idst in instance_ids_in_cell:
            temp1[idst] = {
                "result-open": False,
                "result": cell_inst_output[idst]["Output"],
                "name": idst
            }
        temp = {
            "name": cell,
            "instances-open": False,
            "instances": temp1
        }
        final_output[cell] = temp
    return final_output


def service_domain_to_instance_map(params):
    service_name = params["service_name"]
    domain = params["domain"]
    cell_to_inst_map = {}
    cells_in_domain = domain_to_cell_mapping(service_name, domain)
    
    for cell in cells_in_domain:
        cell_info = {}
        cellidname = cellId_to_instances_mapping(cell)
        for idname in cellidname:
            cell_info[idname] = {
                "selected": False,
                "name": idname
            }
        temp = {
            "selected": False,
            "name": cell,
            "instances-open": False,
            "instances": cell_info
        }
        cell_to_inst_map[cell] = temp
    return {service_name: cell_to_inst_map}


# if __name__ == "__main__":
#     params = {
#         "service_name": "IhmHoustonService",
#         "domain": "gamma"
#     }
#     command_params = {
#         'service_name': 'IhmHoustonService',
#         'instance_info': {
#             'ihm-houston-gamma-pdx': [
#                 'IhmHoustonNativeService-GlobalPreProd AS Group 1 (ID:i-0210b8c15d17664d8)'
#             ],
#             'ihm-houston-gamma-dub': [
#                 'IhmHoustonNativeService-GlobalPreProd AS Group 1 (ID:i-0139dfdd06390272d)'
#             ]
#         },
#         'command': 'ls',
#         'domain': 'gamma'
#     }
#     outp = service_domain_to_instance_map(params)
#     # outp = run_command_on_instances(command_params)
#     print(outp)