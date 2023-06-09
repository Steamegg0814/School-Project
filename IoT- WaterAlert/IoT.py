import RPi.GPIO as GPIO
import socket
import datetime
import time
from hx711 import HX711
import pymysql

GPIO.setwarnings(False)
#Database connection
conn = pymysql.connect(host="172.20.10.4",port=3306,user= "IoT",password= "0000", db="wateralert",charset="utf8")
cursor = conn.cursor()
print('sucessufully connect mysql')

# Wi-Fi Socket settings
host = '172.20.10.2'
port = 8888

Buzzer = 17 
def setup(pin):
    global BuzzerPin
    BuzzerPin = pin
    GPIO.setmode(GPIO.BCM)       # Numbers GPIOs by physical location
    GPIO.setup(BuzzerPin, GPIO.OUT)
#     GPIO.output(BuzzerPin, GPIO.HIGH)
    
def on():
    GPIO.output(BuzzerPin, GPIO.LOW)
    
def off():
    GPIO.output(BuzzerPin, GPIO.HIGH)
    
def beep(x):
    on()
    time.sleep(1)
    off()
    time.sleep(1)
    
def destroy():
    GPIO.output(BuzzerPin, GPIO.HIGH)
    GPIO.cleanup() 


# 設定初始值
amount = 0
previous_weight = 0
alert = False
daily_amount = 0
current_date = datetime.date.today()

# Insert data into MySQL database
def insert_data(conn, table, data):
    query = f"INSERT INTO {table} (Weight, Amount_hour, CurrentTime) VALUES (%s, %s, %s)"
    with conn.cursor() as cursor:
        cursor.execute(query, data)
    conn.commit()
    print('Daily saved!')

# Calculate daily drink amount and save in MySQL database
def update_daily_drink_amount(conn, table, data):
    query = f"INSERT INTO {table} (Amount, Date) VALUES (%s, %s)"
    with conn.cursor() as cursor:
        cursor.execute(query, data)
    conn.commit()
    print('Daily saved!')

    

if __name__ == '__main__': 

    try:
        
        # Start listening for Wi-Fi messages
        server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_socket.bind((host, port))
        server_socket.listen(1)
        print('Listening for Wi-Fi messages...')
        
        while True:
            setup(Buzzer)
            GPIO.setwarnings(False)
            
            conn_socket, address = server_socket.accept()
            print('Acepted connection from %s:%d' %(address[0], address[1]))
            data = conn_socket.recv(1024)
            

            if data == b'Stop':
                print('User stop')
                break
            hx = HX711(5, 6)
            hx.set_reading_format("MSB", "MSB")
            hx.set_reference_unit(150)
            hx.reset()
            hx.tare()
            print("Tare done! Add weight now...")
            time.sleep(1)
            weights = []
            for i in range(10):
                current_weight = max(0, int(hx.get_weight(5)))
                weights.append(current_weight)
                print(current_weight)
                time.sleep(1)   
            # 選擇第10個取樣的重量值
            
            max_weight = weights[9]
            
            sql = "SELECT weight FROM Hourly ORDER BY CurrentTime DESC LIMIT 1"
            cursor.execute(sql)
            result = cursor.fetchone()
            if result is not None:
                previous_weight = 750
            amount = previous_weight - max_weight
            timestamp = time.strftime('%Y-%m-%d %H:%M:%S')
            print(f'Initial weight: {previous_weight} g')
            print(f'Current weight: {current_weight} g')
            
            # Insert data into MySQL database
            insert_data(conn, 'Hourly', (max_weight, amount, timestamp))
            
            # Update daily drink amount
            daily_amount += amount
            print(f'daily_amount: {daily_amount} g')
            
            if amount < 100 and daily_amount < 2000:
                try:
                    beep(1)
                except KeyboardInterrupt:  # When 'Ctrl+C' is pressed, the child program destroy() will be  executed.
                    destroy()    

                print("Alert! DRINK MORE WATER")

            
            if time.strftime("%H:%M") == "23:59":
                update_daily_drink_amount(conn, 'Daily', (daily_amount, current_date))
                daily_amount = 0
            
            time.sleep(5)

           
        
    except KeyboardInterrupt:
        GPIO.cleanup()
        conn.close()