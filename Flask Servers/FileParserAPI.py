from flask import Flask,jsonify,make_response,request
from flask_cors import CORS, cross_origin
from random import randint
import requests
app = Flask(__name__)
cors = CORS(app)
app.config['CORS_HEADERS'] = 'Content-Type'


def random_with_N_digits(n):
    range_start = 10**(n-1)
    range_end = (10**n)-1
    return randint(range_start, range_end)


@app.route("/fileParser", methods=["POST"])
@cross_origin()
def confirm_payment():
    hardCodedStrings = []
    if request.is_json:
        lineNumber = 1
        req = request.get_json()
        file = req.get("input")
        for line in file.splitlines():
            print(line)
            dictToSend = {'input':line}
            res = requests.post('http://localhost:9999/tokenNLP', json=dictToSend)
            if(res.text == "false"):
                innerdict = {}
                innerdict["raw_string"] = line
                innerdict["uuid"] = random_with_N_digits(5)
                innerdict["pos"] = lineNumber
                hardCodedStrings.append(innerdict)
            lineNumber = lineNumber + 1
            print 'response from server:',res.text
        return make_response(jsonify(hardCodedStrings), 200)

    else:
        return make_response(jsonify({"message": "Request body must be JSON"}), 400)

if __name__ == "__main__":
    app.run(port=4444)
