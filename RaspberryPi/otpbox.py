# -*- coding: utf-8 -*-

import RPi.GPIO as GPIO
import time
from pad4pi import rpi_gpio
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
        print ("Start subscribe : otpbox")
    elif mid == 2:
        print ("Start subscribe : otpbox_android")


def on_message(client, userdata, msg):
    topic = msg.topic
    global box1_pw, box1_val, box2_pw, box2_val, box3_pw, box3_val
    # 서버에서받은메시지
    if (topic == "otpbox"):
        print ("Subscribe from server")
        # 박스선택 알고리즘
        if (box1_val == "unusable" and box2_val == "unusable" and box3_val == "unusable"):
            print ("All box unusable")
        else:
            global sub_pw
            # JSON 데이터 추출
            data = msg.payload
            json_data = json.loads(data)
            sub_pw = str(json_data['password'])
            add = str(json_data['address'])

            if (box1_val == "usable"):
                print ("Box1, Change password :", sub_pw, "address :", add)
                box1_pw = sub_pw
                temp = 1
                box1_val = "unusable"
            elif (box2_val == "usable"):
                print ("Box2, Change password :", sub_pw, "address :", add)
                box2_pw = sub_pw
                temp = 2
                box2_val = "unusable"
            elif (box3_val == "usable"):
                print ("Box3, Change password :", sub_pw, "address :", add)
                box3_pw = sub_pw
                temp = 3
                box3_val = "unusable"
            else:
                print ("Error : select box")
                temp = 4

            if (temp != 4):
                dataJson = {"password": sub_pw, "address": add, "boxnumber": str(temp)}
                parseJson = json.dumps(dataJson)
                client.publish("otpbox_rasp", parseJson, 1)

    # 안드로이드에서받은메시지
    elif (topic == "otpbox_android"):
        print ("message from android")


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

for i in range(0, 5):
    client.loop()

# 비동기식 loop
client.loop_start()

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

#서보모터 초기화 부분
GPIO.setmode(GPIO.BCM)

GPIO.setup(12, GPIO.OUT)    
GPIO.setup(16, GPIO.OUT)
GPIO.setup(25, GPIO.OUT)
p0 = GPIO.PWM(12, 50)       #1번 박스 서보모터 핀
p1 = GPIO.PWM(16, 50)
p2 = GPIO.PWM(25, 50)

p0.start(0)
p1.start(0)
p2.start(0)

#키패드 모듈 초기화부분
press=''            #입력 비밀번호 초기화
count=0             #입력 비밀번호 수

#키패드 모듈 매핑
KEYPAD = [
    ["1", "2", "3", "A"],
    ["4", "5", "6", "B"],
    ["7", "8", "9", "C"],
    ["*", "0", "#", "D"]
]

#핀 넘버링
COL_PINS = [17, 15, 14, 4]
ROW_PINS = [24,22,27,18]

#GPIO 매핑
factory = rpi_gpio.KeypadFactory()
keypad = factory.create_keypad(keypad=KEYPAD, row_pins=ROW_PINS, col_pins=COL_PINS)


def printKey(key):                      #키패드 입력시 실행함수
    lcd_byte(ord(key),LCD_CHR)          #1602 LCD 하단 수 출력
    global press                        #입력 비밀번호
    global count                        #입력 비밀번호 수

    if key=='#':                        # '#'은 비밀번호 입력 완료시
        if box1_pw==press:              # 1번 박스 비밀번호부터 비교
            open_box1()
        elif box2_pw==press:
            open_box2()
        elif box3_pw==press:
            open_box3()
        else:
            fail()                      #모든 박스 비밀먼호와 다를시 fail
        press=''                        #초기화
        count=0
    elif key=='*':                      # '*'입력시 초기화
        press=''
        print(str(key))
        count=0
    else:                               #수 입력
        count+=1                       
        press=press+str(key)
        print(press)
        
        
def open_box1():                            #문 개방
    global box1_val
    global p0                           #GPIO 1번 박스 포트
    print("Box number 1 opened")
    p0.ChangeDutyCycle(7.5)             #개방
    box1_val = "unusable"                 #1번 박스 상태 사용중으로 변경
    
def open_box2():
    global box2_val
    global p1
    print("Box number 2 opened")
    p1.ChangeDutyCycle(7.5)
    box2_val = "unusable"
    
def open_box3():
    global box3_val
    global p2
    print("Box number 3 opened")
    p2.ChangeDutyCycle(7.5)
    box3_val = "unusable"


def fail():                             #실패
    print("Password incorrect")
#******************************************#

# 키패드 버튼을 누를때마다 printKey함수 호출
keypad.registerKeyPressHandler(printKey)



#LCD 드라이버
# Define GPIO to LCD mapping
LCD_RS = 21
LCD_E  = 20
LCD_D4 = 26
LCD_D5 = 19
LCD_D6 = 13
LCD_D7 = 6



# Define LCD parameters
LCD_WIDTH = 16    # Maximum characters per line
LCD_CHR = True
LCD_CMD = False

LCD_LINE_1 = 0x80 # LCD RAM address for the 1st line
LCD_LINE_2 = 0xC0 # LCD RAM address for the 2nd line

# Timing constants
E_PULSE = 0.0005
E_DELAY = 0.0005


#******************************************#
def main():
  # Main program block
  global pm
  global system_sts

  
  GPIO.setwarnings(False)
  #GPIO.setmode(GPIO.BCM)       # Use BCM GPIO numbers
  GPIO.setup(LCD_E, GPIO.OUT)  # E
  GPIO.setup(LCD_RS, GPIO.OUT) # RS
  GPIO.setup(LCD_D4, GPIO.OUT) # DB4
  GPIO.setup(LCD_D5, GPIO.OUT) # DB5
  GPIO.setup(LCD_D6, GPIO.OUT) # DB6
  GPIO.setup(LCD_D7, GPIO.OUT) # DB7
 
    

  # Initialise display
  lcd_init()
  lcd_byte(0x01, LCD_CMD)
  lcd_string("    Welcome",LCD_LINE_1)
  print("    Welcome")
  lcd_byte(0xC0, LCD_CMD)
  while True:
      time.sleep(1)

      
#******************************************#  
def lcd_init():
  # Initialise display
  lcd_byte(0x33,LCD_CMD) # 110011 Initialise
  lcd_byte(0x32,LCD_CMD) # 110010 Initialise
  lcd_byte(0x06,LCD_CMD) # 000110 Cursor move direction
  lcd_byte(0x0C,LCD_CMD) # 001100 Display On,Cursor Off, Blink Off
  lcd_byte(0x28,LCD_CMD) # 101000 Data length, number of lines, font size
  lcd_byte(0x01,LCD_CMD) # 000001 Clear display
  time.sleep(E_DELAY)

#******************************************#
def lcd_byte(bits, mode):
  # Send byte to data pins
  # bits = data
  # mode = True  for character
  #        False for command

  GPIO.output(LCD_RS, mode) # RS

  # High bits
  GPIO.output(LCD_D4, False)
  GPIO.output(LCD_D5, False)
  GPIO.output(LCD_D6, False)
  GPIO.output(LCD_D7, False)
  if bits&0x10==0x10:
    GPIO.output(LCD_D4, True)
  if bits&0x20==0x20:
    GPIO.output(LCD_D5, True)
  if bits&0x40==0x40:
    GPIO.output(LCD_D6, True)
  if bits&0x80==0x80:
    GPIO.output(LCD_D7, True)

  # Toggle 'Enable' pin
  lcd_toggle_enable()

  # Low bits
  GPIO.output(LCD_D4, False)
  GPIO.output(LCD_D5, False)
  GPIO.output(LCD_D6, False)
  GPIO.output(LCD_D7, False)
  if bits&0x01==0x01:
    GPIO.output(LCD_D4, True)
  if bits&0x02==0x02:
    GPIO.output(LCD_D5, True)
  if bits&0x04==0x04:
    GPIO.output(LCD_D6, True)
  if bits&0x08==0x08:
    GPIO.output(LCD_D7, True)

  # Toggle 'Enable' pin
  lcd_toggle_enable()

#******************************************#
def lcd_toggle_enable():
  # Toggle enable
  time.sleep(E_DELAY)
  GPIO.output(LCD_E, True)
  time.sleep(E_PULSE)
  GPIO.output(LCD_E, False)
  time.sleep(E_DELAY)

#***************************************#


def lcd_string(message,line):
  # Send string to display

  message = message.ljust(LCD_WIDTH," ")

  lcd_byte(line, LCD_CMD)

  for i in range(LCD_WIDTH):
    lcd_byte(ord(message[i]),LCD_CHR)
    

#******************************************#






    

if __name__ == '__main__':

  try:
    main()
  except KeyboardInterrupt:
    pass
  finally:
    lcd_byte(0x01, LCD_CMD)
    lcd_string("Goodbye!",LCD_LINE_1)
    print("Goodbye!")
    GPIO.cleanup()
