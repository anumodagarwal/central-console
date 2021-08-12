import json
from flask import Flask, jsonify, request, make_response
from flask_cors import CORS
import time
from backend import service_domain_to_instance_map, run_command_on_instances

app = Flask(__name__)
CORS(app)


@app.route("/", methods=["GET"])
def ping():
    return make_response("pong", 200)

@app.route("/cell-info", methods=["POST"])
def cell_info():
    data = json.loads(request.data)
    return make_response(service_domain_to_instance_map(data), 200)


@app.route("/execute-command", methods=["POST"])
def execute_command():
    time.sleep(2)
    data = json.loads(request.data)
    print(f"data: {data}")
    return make_response(run_command_on_instances(data), 200)
    
if __name__ == "__main__":
    app.run(host='0.0.0.0', port=8080)
