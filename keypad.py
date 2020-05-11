#*********************************************************#
#   Author  : iTechno
#   email   : itechnolabz@gmail.com 
#*********************************************************#


#import
import RPi.GPIO as GPIO
import time
from pad4pi import rpi_gpio

#******************************************#
KEYPAD = [
    ["1", "2", "3", "A"],
    ["4", "5", "6", "B"],
    ["7", "8", "9", "C"],
    ["*", "0", "#", "D"]
]

COL_PINS = [17, 15, 14, 4] # BCM numbering
ROW_PINS = [24,22,27,18] # BCM numbering 

sub_pw=None         #서버에서 들어온 OTP

box1_pw = None      #1번 박스 비밀번호
box2_pw = None
box3_pw = None

box1_val = "usable" #1번 박스 사용상태
box2_val = "usable"
box3_val = "usable"

press=''            #입력 비밀번호 초기화
count=0             #입력 비밀번호 수

box=[0,0,0]
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


factory = rpi_gpio.KeypadFactory()
keypad = factory.create_keypad(keypad=KEYPAD, row_pins=ROW_PINS, col_pins=COL_PINS)

#******************************************#

def printKey(key):                      #키패드 입력시 실행함수
    lcd_byte(ord(key),LCD_CHR)          #1602 LCD 하단 수 출력
    global press                        #입력 비밀번호
    global count                        #입력 비밀번호 수

    if key=='#':                        # '#'은 비밀번호 입력 완료시
        if box1_pw==press:              # 1번 박스 비밀번호부터 비교
            open0()
        elif box2_pw==press:
            open1()
        elif box3_pw==press:
            open2()
        else:
            fail()                      #모든 박스 비밀먼호와 다를시 fail
        press=''                        #초기화
        count=0
    elif key=='*':                      # '*'입력시 초기화
        press=''
        count=0
    else:                               #수 입력
        count+=1                       
        press=press+str(key)
        print(press)
        
        
def open0():                            #문 개방
    global p0                           #GPIO 1번 박스 포트
    print("open0")
    p0.ChangeDutyCycle(7.5)             #개방
    box1_val = "usable"                 #1번 박스 상태 사용중으로 변경
    
def open1():
    global p1
    print("open1")
    p1.ChangeDutyCycle(7.5)
    box2_val = "usable"
    
def open2():
    global p2
    print("open2")
    p2.ChangeDutyCycle(7.5)
    box3_val = "usable"

# p2.ChangeDutyCycle(7.5)   open
# p2.ChangeDutyCycle(3)     close

def fail():                             #실패
    print("fail")
#******************************************#

# printKey will be called each time a keypad button is pressed
keypad.registerKeyPressHandler(printKey)




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
