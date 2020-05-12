#-*- coding:utf-8 -*-
import paho.mqtt.client as mqtt
import json


def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("connected OK")
        client.subscribe("otpbox", 1)
        client.subscribe("otpbox_android", 1)
    else:
        print("Bad connection Returned code=", rc)


def on_disconnect(client, userdata, flags, rc=0):
    print(str(rc))


def on_subscribe(client, userdata, mid, granted_qos):
    if mid == 1:
        print "Start subscribe : otpbox"
    elif mid == 2:
        print "Start subscribe : otpbox_android" 


def on_message(client, userdata, msg):
    topic = msg.topic
    global box1_pw, box1_val, box2_pw, box2_val, box3_pw, box3_val
    #서버에서받은메시지
    if(topic == "otpbox"):
        print "Subscribe from server"
        #박스선택 알고리즘
        if(box1_val == "unusable" and box2_val == "unusable" and box3_val == "unusable"):
            print"All box unusable"
        else:
            global sub_pw
            #JSON 데이터 추출
            data = msg.payload
            json_data = json.loads(data)
            sub_pw = str(json_data['password'])    
            add = str(json_data['address'])
            
            if(box1_val == "usable"):
                print"Box1, Change password :",sub_pw,"address :",add
                box1_pw = sub_pw
                temp = 1
                box1_val = "unusable"
            elif(box2_val == "usable"):
                print"Box2, Change password :",sub_pw,"address :",add
                box2_pw = sub_pw
                temp = 2
                box2_val = "unusable"
            elif(box3_val == "usable"):
                print"Box3, Change password :",sub_pw,"address :",add
                box3_pw = sub_pw
                temp = 3
                box3_val = "unusable"
            else:
                print"Error : select box"
                temp = 4
            
            if(temp != 4):
                dataJson = {"password":sub_pw,"address":add,"boxnumber":str(temp)}
                parseJson = json.dumps(dataJson)
                client.publish("otpbox_rasp",parseJson,1)
                
    #안드로이드에서받은메시지
    elif(topic == "otpbox_android"):
        print"message from android"

# 새로운 클라이언트 생성
client = mqtt.Client()
# 콜백 함수 설정 on_connect(브로커에 접속), on_disconnect(브로커에 접속중료), on_subscribe(topic 구독),
# on_message(발행된 메세지가 들어왔을 때)
client.on_connect = on_connect
client.on_disconnect = on_disconnect
client.on_subscribe = on_subscribe
client.on_message = on_message
# 집ip , port: 1883 에 연결
client.connect("211.197.225.191", 1883)
# common topic 으로 메세지 발행

for i in range(0,5):
    client.loop()

#전역변수 선언

#수신한 otp저장
sub_pw = None

#보관함1~3의 비밀번호
box1_pw = None
box2_pw = None
box3_pw = None

#보관함1~3의 상태
box1_val = "usable"
box2_val = "usable"
box3_val = "usable"


#비동기식 loop
client.loop_start()

#키패드모듈, 서보모터, 부저 추가
while True:
    for i in range(0,1000000):
        a=1
    
    

    
