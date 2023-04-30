import time
import datetime
import sqlite3
import RPi.GPIO as GPIO
import Adafruit_DHT
import pymysql
from link import *
from data import 




GPIO.setmode(GPIO.BCM)
GPIO_TRIGGER = 7
GPIO_ECHO = 12
GPIO_TEMP = 14
sensor = Adafruit_DHT.DHT11

GPIO.setup(GPIO_TRIGGER, GPIO.OUT)
GPIO.setup(GPIO_ECHO,GPIO.IN)

def send_trigger_pulse():
    GPIO.output(GPIO_TRIGGER,True)
    time.sleep(0.00001)
    GPIO.output(GPIO_TRIGGER,False)

def distance(speed):
    send_trigger_pulse()
    
    while GPIO.input(GPIO_ECHO)==0:
        StartTime = time.time()
    while GPIO.input(GPIO_ECHO)==1:
        StopTime = time.time()
        
    TimeElapsed = StopTime - StartTime
    distance = (TimeElapsed*speed)/2
    
    return distance

def get_speed():
    speed = 33100+15*60
    return speed

def distance(speed):
    send_trigger_pulse()

    while GPIO.input(GPIO_ECHO)==0:
        StartTime = time.time()
    while GPIO.input(GPIO_ECHO)==1:
        StopTime = time.time()
        
    TimeElapsed = StopTime - StartTime
    distance = (TimeElapsed*speed)/2  
    return distance

def Setup(GPIOnum, OUT_IN):
    GPIO.setmode(GPIO.BCM) 
    if OUT_IN == "OUT":
        GPIO.setup(GPIOnum, GPIO.OUT)#setup GPIO I/O port
    else:
        GPIO.setup(GPIOnum, GPIO.IN)#setup GPIO I/O port
    
def TurnOnLED(GPIOnum):
    GPIO.output(GPIOnum, True)
    
def TurnOffLED(GPIOnum):
    GPIO.output(GPIOnum, False) 

def GetGPIOStatus(GPIOnum):
    GPIO_State = GPIO.input(GPIOnum)# get GPIO port status
    return GPIO_State


if __name__ =='__main__':
    try:
        while True:
            #每5秒存一次到sqlite，所以5分鐘要跑60次，存到mysql
            for index in range (61):
                if index < 60 :## index = 2 sec = 10,index 4 sec=20, 5min index= 61
                    Setup(2, "IN")
                    Setup(2, "OUT")
                    Setup(3, "IN")
                    Setup(3, "OUT")
                    Setup(4, "IN")
                    Setup(4, "OUT")
                    TurnOffLED(2)
                    TurnOffLED(3)
                    TurnOffLED(4)              
                    
                    #每五秒存一次data到sqlite
                    for i in range(4):
                        speed = get_speed()
                        dist1 = distance(speed)
                        time.sleep(1)
                        dist2 = distance(speed)
                        #秒數
                        litespeed = int(abs(dist2-dist1))
                        if (litespeed > 30):
                           TurnOnLED(2)
                           time.sleep(0.1)                
                        elif (20 < litespeed and litespeed <= 30):
                           TurnOnLED(3)
                           time.sleep(0.1)
                        elif(10 < litespeed and litespeed <= 20):
                           TurnOnLED(4)
                           time.sleep(0.1)                
                        else:
                           TurnOffLED(2)
                           TurnOffLED(3)
                           TurnOffLED(4)
                        print("speedPerSecond= %.lf cm"%litespeed)
                        #存data到sqlite
                        if (i==3) :
                            litetime = datetime.datetime.now().strftime("%Y-%m-%d-%H:%M:%S")
                            sql  = "INSERT INTO speedAlert VALUES (litetime,litespeed)“
                            c.execute(sql)
                            conn.commit()
                        time.sleep(0.2)
                else:
                    data = c.execute("SELECT * FROM speedAlert")
                    for i in data:
                        try:
                            sql  = f"INSERT INTO speedAlert VALUES {i}"
                            cursor.execute(sql)
                            db.commit()
                        except:
                            continue
                    continue
                
    except KeyboardInterrupt:
        print("Measurement stopped by User")
        GPIO.cleanup()
    conn.close()
    db.close()