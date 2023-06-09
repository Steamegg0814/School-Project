from flask import Flask, render_template
import pymysql
import time
import random
import datetime
import pandas as pd
from itertools import chain
import matplotlib.pyplot as plt
 
db = pymysql.connect(host="172.20.10.4",port=3306,user= "IoT",password= "0000", db="Wateralert",charset="utf8")
cursor = db.cursor()

# 設定起始日期和時間
start_date = datetime.datetime(2023, 5, 30)
hours_per_day = 24
num_days = 10

# 設定初始權重
current_weight = 2000

# 設定起始日期和時間
start_date = datetime.datetime(2023, 6, 3, 9)  # 從早上9點開始
hours_per_day = 24
num_days = 6

# 生成模擬資料
daily_stats = {}  # 用於儲存每天的amount_hour總和
for day in range(num_days):
    current_date = start_date + datetime.timedelta(days=day)
    current_weight = 2000 # 每天開始時重置current_weight為2000
    daily_amount_hour = 0  # 每天的amount_hour總和
    for hour in range(hours_per_day):
        current_time = current_date + datetime.timedelta(hours=hour)
        current_time_str = current_time.strftime("%Y-%m-%d %H:%M:%S")
        previous_weight = current_weight
        current_weight -= random.randint(20, 150)  # 遞減的隨機數字
        if current_weight < 0:
            current_weight = 0  # 確保權重不為負數
        amount_hour = previous_weight - current_weight  # 上一次測量的 weight 減去這一次測量的 weight
        # alert = 1 if amount_hour < 100 and daily_amount_hour < 2000 else 0  # 根據 Amount_hour 更改
        query = "INSERT INTO Hourly (CurrentTime, Weight, Amount_hour) VALUES (%s, %s, %s)"
        values = (current_time_str, current_weight, amount_hour)
        cursor.execute(query, values)
        daily_amount_hour += amount_hour  # 每小時的amount_hour累加到每天的總和
    
    # 儲存每天的amount_hour總和到daily_stats字典中
    daily_stats[current_date.date()] = daily_amount_hour

# 將daily_stats中的資料插入到daily_stats資料表中
for date, amount_hour_sum in daily_stats.items():
    query = "INSERT INTO Daily (Date, Amount) VALUES (%s, %s)"
    values = (date, amount_hour_sum)
    cursor.execute(query, values)

# 提交並關閉資料庫連接
db.commit()
cursor.close()
db.close()
