import pandas as pd
import numpy as np
import datetime
import matplotlib.pyplot as plt
from matplotlib.pyplot import figure
import hvplot.pandas
from sklearn.preprocessing import MinMaxScaler
from xgboost import XGBRegressor
from sklearn import metrics
from sklearn.metrics import confusion_matrix,accuracy_score
from sklearn.preprocessing import minmax_scale

stock = pd.read_csv(r'stockday.csv')
order = ['date', 'volume', 'open', 'high', 'low', 'close','change','change_stock', 'change_stock_scaled']
stock = stock[order]
stock['date'] = pd.to_datetime(stock['date'])
sentiment = pd.read_csv(r'sentiment.csv')
sentiment.rename(columns = {'time':'date'}, inplace = True)
sentiment['date'] = pd.to_datetime(sentiment['date'])

mergedf = pd.merge(sentiment,stock, on='date',how='outer')
mergedf = mergedf.set_index('date',inplace = False)
mergedf = mergedf.interpolate()
#mergedf.to_csv('mergedf.csv')
#mergedf = mergedf.reset_index()
mergedf

#stock sentiment走勢
figure(figsize=(30, 10))
fig, ax_left = plt.subplots()
ax_left.plot(mergedf.date,mergedf.close,label='stock.close', color='#2A4C65')
ax_left.set_ylabel('stock')

ax_right = ax_left.twinx()
ax_right.plot(mergedf.date, mergedf.sentiment, label='sentiment', color='#D3AC73')
ax_right.set_ylabel('sentiment')
fig.autofmt_xdate(rotation=45)
plt.savefig('stock_sentiment line plot.jpg',dpi=500)

df = mergedf[["close", "sentiment"]]
#df["pct_change"] = df["close"].pct_change()
df.dropna(inplace = True)
df.head()


def window_data(df, window, feature_col_number1, feature_col_number2,target_col_number):
    X_close = []
    X_sentiment = []
    y = []
    for i in range(len(df) - window):
        
        close = df.iloc[i:(i + window), feature_col_number1]
        sentiment = df.iloc[i:(i + window), feature_col_number2]
        target = df.iloc[(i + window), target_col_number]
        
        # Append values in the lists
        X_close.append(close)
        X_sentiment.append(sentiment)
        y.append(target)
        
    return np.hstack((X_close,X_sentiment)), np.array(y).reshape(-1, 1)

window_size = 30
feature_col_number1 = 0
feature_col_number2 = 1
target_col_number = 0
X, y = window_data(df, window_size, feature_col_number1, feature_col_number2, target_col_number)
#切 train split
x_split = int(0.7 * len(X))
y_split = int(0.7 * len(y))
x_train = X[: x_split]
x_test = X[x_split:]
y_train = y[: y_split]
y_test = y[y_split:]
#要做XGB要先scale data
x_train_scaler = MinMaxScaler()
x_test_scaler = MinMaxScaler()
y_train_scaler = MinMaxScaler()
y_test_scaler = MinMaxScaler()

#特徵
x_train_scaler.fit(x_train)
y_train_scaler.fit(y_train)
x_train = x_train_scaler.transform(x_train)
y_train = y_train_scaler.transform(y_train)

#label
x_test_scaler.fit(x_test)
y_test_scaler.fit(y_test)

#label
x_test = x_test_scaler.transform(x_test)
y_test = y_test_scaler.transform(y_test)

#model
model = XGBRegressor(objective='reg:squarederror', n_estimators=1000)
model.fit(x_train, y_train.ravel())

predict_test = model.predict(x_test)
predict_train = model.predict(x_train)    
print('RMSE_test:', np.sqrt(metrics.mean_squared_error(y_test, predict_test)))
print('RMSE_train:', np.sqrt(metrics.mean_squared_error(y_train, predict_train)))
# Recover the original prices instead of the scaled version
predicted_prices = y_test_scaler.inverse_transform(predict_test.reshape(-1, 1))
real_prices = y_test_scaler.inverse_transform(y_test.reshape(-1, 1))

#相關性分析
df['close'] = minmax_scale(df['close'])
corr_df = df.corr(method='pearson')
#reset symbol as index (rather than 0-X)
corr_df.head().reset_index()
corr_df.head(10)
#take the bottom triangle since it repeats itself
mask = np.zeros_like(corr_df)
mask[np.triu_indices_from(mask)] = True
import seaborn
plt.figure(figsize=(10,8))

g = seaborn.heatmap(corr_df, cmap='Blues', vmax=1.0, vmin=-1.0 , mask = mask, linewidths=2.5,annot = True)
plt.yticks(rotation=0) 
plt.xticks(rotation=30) 
plt.savefig('correlation.jpg',dpi=500)

# 轉回原始data
stocks = pd.DataFrame({
    "real": real_prices.ravel(),
    "predict": predicted_prices.ravel()
}, index = df.index[-len(real_prices): ]) 
stocks.head()

stocks.hvplot(title = "sentiment&stock XGB prediction" )