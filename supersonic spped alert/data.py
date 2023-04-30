from link import *
import pymysql
import sqlite3

def execute():
        c.execute(sql)
        return c

def sqlite_insert(litetime,litespeed):
        sql = 'INSERT INTO speedAlert VALUES (litetime,litespeed)'
        c.execute(sql)
        conn.commit()

def sqlite_get():
        sql = 'SELECT * FROM speedAlert'
        c.execute(sql)
        conn.commit()

def sql_insert():
        sql = f"INSERT INTO speedAlert VALUES {i}"
        cursor.execute(sql)
        db.commit()