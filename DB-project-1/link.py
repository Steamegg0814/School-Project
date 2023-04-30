import getpass
import oracledb

connection = oracledb.connect(
    user="GROUP3",
    password='9bMnh4jMl0',
    dsn="140.117.69.60/ORCLPDB1")

print("Successfully connected to Oracle Database")
cursor = connection.cursor()
