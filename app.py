import json
from flask import Flask, jsonify, request, make_response
from flask_cors import CORS

app = Flask(__name__)
CORS(app)


@app.route("/", methods=["GET"])
def ping():
    return make_response("pong", 200)

@app.route("/cell-info", methods=["POST"])
def index():
    data = json.loads(request.data)
    service_name = data["service_name"]
    domain = data["domain"]
    print(f"Received service_name: {service_name}, domain: {domain}")
    cell_info = {
        "ihm-dub":
        {
            "selected": false,
            "name": "IHM-DUB",
            "instances-open": false,
            "instances":
            {
                "id1":
                {
                    "selected": false,
                    "name": "id1"
                },
                "id2":
                {
                    "selected": false,
                    "name": "id2"
                }
            }
        },
        "ihm-pdx":
        {
            "selected": false,
            "name": "IHM-PDX",
            "instances-open": false,
            "instances":
            {
                "id1":
                {
                    "selected": false,
                    "name": "id1"
                },
                "id2":
                {
                    "selected": false,
                    "name": "id2"
                }
            }
        }
    }
    return make_response(cell_info, 200)

if __name__ == "__main__":
    app.run()
