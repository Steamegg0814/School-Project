#ARIMA predict
#json格式處理
import json
import pandas as pd
from pandas.io.json import json_normalize
from pandas import Series, DataFrame

with open('時報_三年股票Ticker歷史資料_0050.json',encoding = 'utf-8') as data_file:    
    df = json.load(data_file)
    
ndf = pd.json_normalize(df["data"]["content"]["rawContent"]["day"])
#抓取時間範圍2020/2/1-2022
ndf = ndf[(ndf['date'] >= '2020-02-01') & (ndf['date'] <= '2022-12-21')]
ndf['date'] = pd.to_datetime(ndf['date']).dt.date
ndf['change_stock'] = (ndf['close'] - ndf['open']) / ndf['open'] * 100.0
#ndf.to_csv('stockday.csv')



import numpy as np
import pandas as pd
from datetime import datetime
from pandas import datetime

import matplotlib.pyplot as plt

from statsmodels.tsa.stattools import acf, pacf
from statsmodels.graphics.tsaplots import plot_acf, plot_pacf
from statsmodels.tsa.arima_model import ARIMA
import statsmodels.api as sm
from sklearn.metrics import mean_squared_error

import warnings

warnings.filterwarnings('ignore')
plt.style.use('seaborn-poster')

stock = pd.read_csv(r'stockday.csv')
order = ['date', 'volume', 'open', 'high', 'low', 'close','change','change_stock', 'change_stock_scaled']
stock = stock[order]
stock['date'] = pd.to_datetime(stock['date'])
close = stock['close']

#30日移動平均
rolLmean = close.rolling(30).mean()
rolLstd = close.rolling(30).std()

plt.figure(figsize=(16,7))
fig = plt.figure(1)

orig = plt.plot(close,label='close price')
mean = plt.plot(rolLmean, color='red', label='Rolling Mean')
std = plt.plot(rolLstd, color='black', label = 'Rolling Std')
plt.legend(loc='best')
plt.title('Rolling Mean & Std')
plt.show(block=False)

#確認時序的 stationarity
plt.figure(figsize=(16,7))
fig = plt.figure(1)
#取log
close_log = np.log(close)
plt.plot(close_log)
#differencing
#一階差分
plt.figure(figsize=(16,7))
fig = plt.figure(1)
close_log_diff = close_log - close_log.shift()
plt.plot(close_log_diff)

rolLmean = close_log_diff.rolling(30).mean()
rolLstd = close_log_diff.rolling(30).std()

orig = plt.plot(close_log_diff, color='#4798B3',label='close')
mean = plt.plot(rolLmean, color='#FFBF00', label='Rolling Mean')
std = plt.plot(rolLstd, color='black', label = 'Rolling Std')
plt.legend(loc='best')
plt.title('Rolling Mean & Std')
plt.show(block=False)
close.sort_index(inplace= True)
#lag取30
lag_acf = acf(close_log_diff, nlags=30)
lag_pacf = pacf(close_log_diff, nlags=30)
fig = plt.figure(figsize=(16,12))
ax1 = fig.add_subplot(211)
fig = sm.graphics.tsa.plot_acf(close_log_diff.dropna(),lags=40,ax=ax1) #d取1
ax2 = fig.add_subplot(212)
fig = sm.graphics.tsa.plot_pacf(close_log_diff.dropna(),lags=40,ax=ax2) #p取1

from statsmodels.tsa.arima_model import ARIMA
model1 = ARIMA(close_log,order=(1,1,0))  #AR模型
model2 = ARIMA(close_log,order=(0,1,1))   #MA模型
model3 = ARIMA(close_log,order=(1,1,1))  #ARIMA模型

result_AR = model1.fit(disp=-1)
result_MA = model2.fit(disp=-1)
result_ARIMA = model3.fit(disp=-1)

plt.plot(result_AR.fittedvalues,color='red',label='result_AR',linewidth = 1)
plt.plot(result_MA.fittedvalues,color='black',label='result_MA',alpha = 0.8,linewidth = 1)
plt.plot(result_ARIMA.fittedvalues,color='green',label='result_ARIMA',alpha = 0.8,linewidth = 1)
plt.legend()
plt.show()

predictions_ARIMA_diff = pd.Series(result_ARIMA.fittedvalues,copy=True)
predictions_ARIMA_diff_cumsum = predictions_ARIMA_diff.cumsum()
predictions_ARIMA_log = pd.Series(close_log.iloc[0], index=close_log.index)
predictions_ARIMA_log = predictions_ARIMA_log.add(predictions_ARIMA_diff_cumsum,fill_value=0)
predictions_ARIMA = np.exp(predictions_ARIMA_log)

plt.plot(close,color='blue',label='original data')
plt.plot(predictions_ARIMA,color='green',label='ARIMA prediction')
plt.legend()


plt.figure(figsize=(16,8))
predictions_ARIMA = np.exp(ARIMA_log_prediction)
plt.plot(close,linewidth = 1.5)
plt.plot(predictions_ARIMA,linewidth = 1.5)
plt.title('RMSE: %.4f'% np.sqrt(sum((ARIMA_log_prediction-close_log)**2)/len(close_log)))

#調整回原本的數字
pred = pd.Series(res.fittedvalues, copy=True)
pred_cumcum = pred.cumsum()
ARIMA_log_prediction = pd.Series(close_log.iloc[0], index=close_log.index)
ARIMA_log_prediction = ARIMA_log_prediction.add(pred_cumcum,fill_value=0)
ARIMA_log_prediction.head()
