import pandas as pd
import numpy as np
import os

#Shopline data
dir=r"\\" 
filenames = os.listdir(dir)
index=0
newdfs=[]
for name in filenames:
    newdfs.append(pd.read_excel(os.path.join(dir,name),usecols=[
        '訂單號碼','顧客 ID','顧客','電郵','電話號碼','送貨狀態','訂單合計','訂單日期','會員等級','商品貨號','商品結帳價','數量','商品類型']))
newdfs = pd.concat(newdfs)
#交換欄順序
newdfs = newdfs[['訂單號碼','顧客 ID','顧客','電郵','電話號碼','送貨狀態','訂單合計','訂單日期','商品貨號','商品結帳價','數量','商品類型','會員等級']]
newdfs.columns = ['訂單編號','會員ID','會員姓名','電郵','電話號碼','送貨狀態','訂單金額','訂單日期','商品貨號','商品結帳價','數量','商品類型','會員等級']
newdfs['會員ID'] = newdfs['會員ID'].astype(str)


#JOO Data
dir=r"\\" 
filenames = os.listdir(dir)
index=0
olddfs=[]
for name in filenames:
    olddfs.append(pd.read_excel(os.path.join(dir,name),usecols=['訂單編號','會員編號','會員姓名','會員帳號','出貨單狀態','訂單金額','交易日期','商品原始貨號','數量','單品實際金額(小計/數量)'
]))
olddfs = pd.concat(olddfs)
#交換欄順序
olddfs = olddfs[['訂單編號','會員編號','會員姓名','會員帳號','出貨單狀態','訂單金額','交易日期','商品原始貨號','單品實際金額(小計/數量)','數量']]
olddfs.columns = ['訂單編號','會員ID','會員姓名','電郵','送貨狀態','訂單金額','訂單日期','商品貨號','商品結帳價','數量']



#利用電郵join新舊官網會員
merge = pd.merge(olddfs,newdfs,on='電郵',how='left')
#篩選顧客id為空的值將顧客id帶入會員編號
merge.loc[(merge['會員ID_y'].notnull()) , '會員ID_x'] = merge['會員ID_y']
#捨棄不需要的欄位 
merge = merge.drop(['訂單編號_y','會員ID_y','會員姓名_y','送貨狀態_y','訂單金額_y','訂單日期_y'],axis=1)
merge = merge.rename(columns={'訂單編號_x':'訂單編號','會員ID_x':'會員ID','會員姓名_x':'會員姓名','送貨狀態_x':'送貨狀態','訂單金額_x':'訂單金額', '訂單日期_x':'訂單日期'})

#append
alldfs = merge.append(newdfs)
#刪除無效訂單
filt = (alldfs['送貨狀態']=='已出貨')|(alldfs['送貨狀態']=='已送達')|(alldfs['送貨狀態']=='已發貨')|(alldfs['送貨狀態']=='已到達')
alldfs = alldfs.loc[filt]
#轉為時間格式
alldfs['訂單日期'] = pd.to_datetime(alldfs['訂單日期'])
alldfs.sort_values(by='訂單日期')
alldfs.index = range(len(alldfs))

#產出不重複電子郵件名單
ordered_customer = alldfs.drop_duplicates('會員ID',inplace = False)
ordered_customer = ordered_customer[['會員ID']]
ordered_customer.to_excel(r'\每日下載檔案\有訂購記錄所有會員.xlsx')


#列出會員購買產品
uniqueproduct = alldfs.dropna(subset = ['商品貨號'])
uniqueproduct.drop_duplicates(subset=['會員ID','電郵','商品貨號'],inplace = True)

items = alldfs.groupby(['會員ID','電郵'])\
.agg({'商品貨號': lambda x: x.ravel().tolist()}).reset_index()
items.to_excel(r"\每日下載檔案\會員購買貨號.xlsx")
items.to_excel(r"\\會員購買貨號.xlsx")



#RFM
#ordertimes

import datetime
#R
now = pd.datetime.now()
alldfsRecency = alldfs.groupby(['會員ID'],as_index=False)['訂單日期','電郵'].max()
alldfsRecency.columns = ['會員ID','LastPurchase','電郵']
alldfsRecency['Recency'] = alldfsRecency.LastPurchase.apply(lambda x:(now - x).days)
alldfsRecency['Recency']

#Firstorder
alldfsFirstorder = alldfs.groupby(['會員ID'],as_index=False)['訂單日期','電郵'].min()
alldfsFirstorder.columns = ['會員ID','Firstorder','電郵']
Firstnlast = alldfsRecency.merge(alldfsFirstorder,left_on='會員ID',right_on='會員ID')
Firstnlast = Firstnlast.drop(['電郵_y'],axis=1)
Firstnlast = Firstnlast.rename(columns={'電郵_x':'電郵'})
Firstnlast['LifeTime'] = (alldfsRecency['LastPurchase'] - alldfsFirstorder['Firstorder']).apply(lambda x: x.days)
Firstnlast = Firstnlast[['會員ID','電郵','Firstorder','LastPurchase','Recency','LifeTime']]


#FM
FM = alldfs.groupby('會員ID').agg({'訂單編號'   : lambda x:len(x),
                                   '訂單金額'  : lambda x:x.sum(),
                                })
FM.columns = ['Frequency','Monetary'] 


#購買週期
Orderascending=alldfs.sort_values(['會員ID','訂單日期'],ascending=True)
#取下一筆資料
OrderascendingNext=Orderascending.groupby('會員ID').shift(1).rename(columns={'訂單日期':'訂單日期_Last'})
OrderascendingNext=OrderascendingNext.rename(columns={'訂單編號':'訂單編號_x','會員姓名':'會員姓名_x','電郵':'電郵_x','會員姓名':'會員姓名_x','送貨狀態':'送貨狀態_x','訂單金額':'訂單金額_x','會員等級':'會員等級_x','電話號碼':'電話號碼_x'})
OrderascendingRes = pd.concat([Orderascending,OrderascendingNext],axis=1)
OrderascendingRes = OrderascendingRes.drop(['訂單編號_x','會員姓名_x','電郵_x','送貨狀態_x','訂單金額_x','會員等級_x','電話號碼_x'],axis=1)
Retention = OrderascendingRes['訂單日期']-OrderascendingRes['訂單日期_Last']
OrderascendingRes['Retention']= (OrderascendingRes['訂單日期']-OrderascendingRes['訂單日期_Last']).apply(lambda x: x.days)
OrderascendingRes = OrderascendingRes.dropna(subset=['Retention'])
OrderascendingRes = round(OrderascendingRes.groupby('會員ID').agg({'Retention'  : ['mean','std']})).reset_index()
OrderascendingRes.columns=['會員ID','Retention','Retentionstd']
              
#RFM
RFM = Firstnlast.merge(FM,left_on='會員ID',right_on='會員ID')
RFM = RFM.merge(OrderascendingRes,how='left',left_on='會員ID',right_on='會員ID')
RFM = RFM[['會員ID','電郵','Firstorder','LastPurchase','Recency','Frequency','Monetary','LifeTime','Retention','Retentionstd']]

#RFM
#Export
today = datetime.date.today().strftime("%Y%m%d")
RFM.to_excel(r"\\RFM\RFM"+today+".xlsx")