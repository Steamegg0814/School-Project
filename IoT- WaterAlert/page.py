from flask import Flask
from flask import render_template
import pymysql

app = Flask(__name__, template_folder='/Users/steamegg/Desktop/NSYSU/112 IoT/Final project/templates')

# 連接到MySQL資料庫
db = pymysql.connect(host="172.20.10.4",port=3306,user= "IoT",password= "0000", db="Wateralert",charset="utf8")

@app.route('/')
def chart():
    cursor = db.cursor()
    query = "SELECT Weight, Amount_hour, CurrentTime FROM Hourly"
    cursor.execute(query)
    data = cursor.fetchall()

    cursor = db.cursor()
    query = "SELECT Amount, Date FROM Daily"
    cursor.execute(query)
    data_Daily = cursor.fetchall()

    # list

    amount_hours = [i[1] for i in data]
    CurrentTime = [i[2] for i in data] 
    amount_daily = [i[0] for i in data_Daily]
    date_daily = [i[1] for i in data_Daily]

    cursor.close()
    db.close()

    return render_template('chart.html', amount_hours=amount_hours, CurrentTime=CurrentTime, amount_daily = amount_daily, date_daily = date_daily )

if __name__ == '__main__':
    app.debug = True
    app.run()
