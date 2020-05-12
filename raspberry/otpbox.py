#-*- coding:utf-8 -*-
import paho.mqtt.client as mqtt
from datetime import datetime, timedelta
import json


def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("connected OK")
    else:
        print("Bad connection Returned code=", rc)


def on_disconnect(client, userdata, flags, rc=0):
    print(str(rc))


def on_subscribe(client, userdata, mid, granted_qos):
    print("Start subscribe : otpbox")


def on_message(client, userdata, msg):
    global time_createOTP # otp를 생성한 시간 기록
    global sub_pw
    time_createOTP = datetime.now()
    print(time_createOTP)
    print("Open the OTPbox until 60sec.")
    
    #JSON 데이터 추출
    data = msg.payload.decode("utf-8")
    json_data = json.loads(data)
    sub_pw = str(json_data['password'])
    
    print "New password :",sub_pw
    
    add = str(json_data['address'])
    print "Address : ",add
#    client.loop_stop()
#    print("loop stop");


#otp를 생성한 후 시간초과 확인함수(시간초과일 경우 -1, 아닐경우 1을 반환)
def timeout():
    time_now = datetime.now()
    global time_createOTP
    if( (time_now - time_createOTP).seconds >= 60 ):
        return -1
    else:
        return 1


# 새로운 클라이언트 생성
client = mqtt.Client()
# 콜백 함수 설정 on_connect(브로커에 접속), on_disconnect(브로커에 접속중료), on_subscribe(topic 구독),
# on_message(발행된 메세지가 들어왔을 때)
client.on_connect = on_connect
client.on_disconnect = on_disconnect
client.on_subscribe = on_subscribe
client.on_message = on_message
# address : localhost, port: 1883 에 연결
client.connect("211.197.225.191", 1883)
# common topic 으로 메세지 발행
client.subscribe("otpbox", 1)
for i in range(0,5):
    client.loop()

#전역변수 선언
time_createOTP = datetime(2020,1,1,1,1,1)
sub_pw = None

#비동기식 loop
client.loop_start()

#키패드모듈, 서보모터, 부저 추가
while True:
    print(sub_pw)
    for i in range(0,1000000):
        a=1
    
    

    
