# coding=utf-8
from flask import Flask,jsonify,make_response,request
from flask_cors import CORS, cross_origin
from random import randint
import requests
import random
app = Flask(__name__)
cors = CORS(app)
app.config['CORS_HEADERS'] = 'Content-Type'

@app.route("/fakeGoogleTranslate", methods=["POST"])
@cross_origin()
def fake_translation_service():
    hardCodedStrings = []
    pr = ["seqüência de texto aleatória ou números aleatórios e texto e símbolos com todos os tipos de caracteres","Mas isso vai além do número de visitantes e mensagens aleatórias.","Olá Mundo esta é a América, a maior nação!","Lorem Ipsum não é simplesmente um texto randômico."]
    rm = ["Conform procedurii prevăzute în art. 16, se adoptă modalităţile generale de punere în aplicare a prezentului alineat.","FORMATUL PENTRU NOTIFICAREA ÎN TEMEIUL ARTICOLULUI 22 PUNCTUL 1","Amendamentul 256 ARTICOLUL 118 AL DOILEA PARAGRAF","AUTORITĂȚILE MENȚIONATE LA ARTICOLUL 2 ALINEATUL","DEFINIȚIA UNEI ÎNTREPRINDERI (APLICAREA PUNCTULUI"]
    fe = ["Wir sind eine ganz normale Familie","Ich wohne zusammen mit meinen","Eltern, meiner kleinen Schwester Lisa und unserer Katze Mick","Oma Francis arbeitet noch. Sie ist Krankenschwester.","Die Anderen sind schon in Rente.","Oma Lydia nimmt sich viel Zeit für mich und geht häufig mit mir Kleider oder Schuhe kaufen","Bank und fährt am Wochenende gern mit seinem Motorrad. Das findet meine Mutter nicht so gut"]
    if request.is_json:
        
        innerdict = {}
        innerdict["translation"] = random.choice(pr)
        innerdict["region"] = "pr"
        hardCodedStrings.append(innerdict)
        
        innerdict = {}
        innerdict["translation"] = random.choice(rm)
        innerdict["region"] = "rm"
        hardCodedStrings.append(innerdict)
        
        innerdict = {}
        innerdict["translation"] = random.choice(fe)
        innerdict["region"] = "fe"
        hardCodedStrings.append(innerdict)

        return make_response(jsonify(hardCodedStrings), 200)

    else:
        return make_response(jsonify({"message": "Request body must be JSON"}), 400)

if __name__ == "__main__":
    app.run(port=6666)
