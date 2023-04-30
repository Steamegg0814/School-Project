from dotenv import load_dotenv
import os 
import pymysql
import sqlite3


load_dotenv()
host = os.getenv("HOST")
port = os.getenv("PORT")
password = os.getenv("PASSWORD")

###connect sqlite##
conn = sqlite3.connect('~/Sqlite/test.db')
print ("成功連接sqlite")
c = conn.cursor()


###connect mysql##
db = pymysql.connect(host = host,port = port,user = "IoT",password = password, db="speedAlert",charset="utf8")
print ("成功連接mysql")
cursor = db.cursor()