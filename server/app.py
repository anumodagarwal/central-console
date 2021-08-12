import json
from flask import Flask, jsonify, request, make_response
from flask_cors import CORS
import time

app = Flask(__name__)
CORS(app)


@app.route("/", methods=["GET"])
def ping():
    return make_response("pong", 200)

@app.route("/cell-info", methods=["POST"])
def cell_info():
    time.sleep(2)
    data = json.loads(request.data)
    service_name = data["service_name"]
    domain = data["domain"]
    print(f"Received service_name: {service_name}, domain: {domain}")
    cell_info = {
        "IhmHoustonService": {
            "ihm-dub":
            {
                "selected": False,
                "name": "IHM-DUB",
                "instances-open": False,
                "instances":
                {
                    "id1":
                    {
                        "selected": False,
                        "name": "id1"
                    },
                    "id2":
                    {
                        "selected": False,
                        "name": "id2"
                    }
                }
            },
            "ihm-pdx":
            {
                "selected": False,
                "name": "IHM-PDX",
                "instances-open": False,
                "instances":
                {
                    "id1":
                    {
                        "selected": False,
                        "name": "id1"
                    },
                    "id2":
                    {
                        "selected": False,
                        "name": "id2"
                    }
                }
            }
        }
    }
    return make_response(cell_info, 200)


@app.route("/execute-command", methods=["POST"])
def execute_command():
    time.sleep(2)
    data = json.loads(request.data)
    service_name = data["service_name"]
    command = data["command"]
    domain = data["domain"]
    instance_info = data["instance_info"]
    print(f"data: {data}")
    execution_result = {
        "ihm-dub":
        {
            "name": "IHM-DUB",
            "instances-open": False,
            "instances":
            {
                "id1":
                {
                    "result-open": False,
                    "result": "Hello this is a text",
                    "name": "id1"
                },
                "id2":
                {
                    "name": "id2",
                    "result-open": False,
                    "result": "Hello this is a text"
                }
            }
        },
        "ihm-pdx":
        {
            "name": "IHM-PDX",
            "instances-open": False,
            "instances":
            {
                "id1":
                {
                    "result-open": False,
                    "result": "Hello this is a text",
                    "name": "id1"
                },
                "id2":
                {
                    "name": "id2",
                    "result-open": False,
                    "result": "Hello this is a text"
                }
            }
        }
    }
    return make_response(execution_result, 200)
    
if __name__ == "__main__":
    app.run()
