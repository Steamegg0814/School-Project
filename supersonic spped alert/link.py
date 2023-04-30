###connect sqlite##
conn = sqlite3.connect('~/Sqlite/test.db')
print ("成功連接sqlite")
c = conn.cursor()


###connect mysql##
db = pymysql.connect(host="",port=0000,user= "IoT",password= "", db="speedAlert",charset="utf8")
print ("成功連接mysql")
cursor = db.cursor()