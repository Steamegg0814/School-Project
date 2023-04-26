library(ggplot2);library(data.table);library(vcd);library(grid)
song_data = read.csv("Dataset/final_3.csv", header = T, encoding = 'UTF-8')
View(song_data)
my_explicit = xtabs(~ explicit + HOT100,data = song_data)
addmargins(prop.table(my_explicit))
#HOT100
#explicit           0           1         Sum
#0   0.866990190 0.010634523 0.877624713
#1   0.117011062 0.005364225 0.122375287
#Sum 0.984001252 0.015998748 1.000000000
my_key = xtabs(~ key + HOT100,data = song_data)
addmargins(prop.table(my_key))
#HOT100
#key              0            1          Sum
#0   0.1145898560 0.0018680860 0.1164579420
#1   0.0917031935 0.0022020455 0.0939052390
#2   0.0971613442 0.0012836569 0.0984450010
#3   0.0317157170 0.0004591943 0.0321749113
#4   0.0774994782 0.0009705698 0.0784700480
#5   0.0764036736 0.0012940931 0.0776977666
#6   0.0706219996 0.0011897307 0.0718117303
#7   0.1140367355 0.0018054686 0.1158422041
#8   0.0621895220 0.0012001670 0.0633896890
#9   0.1018680860 0.0013462743 0.1032143603
#10  0.0626904613 0.0008348988 0.0635253601
#11  0.0835211856 0.0015445627 0.0850657483
#Sum 0.9840012523 0.0159987477 1.0000000000
my_mode = xtabs(~ mode + HOT100,data = song_data)
addmargins(prop.table(my_mode))
#HOT100
#mode            0           1         Sum
#0   0.393550407 0.005604258 0.399154665
#1   0.590450845 0.010394490 0.600845335
#Sum 0.984001252 0.015998748 1.000000000
my_time_signature = xtabs(~ time_signature + HOT100,data = song_data)
addmargins(prop.table(my_time_signature))
#HOT100
#time_signature            0            1          Sum
#0   1.492382e-03 0.000000e+00 1.492382e-03
#1   5.270298e-03 1.043623e-05 5.280735e-03
#3   6.069714e-02 5.322480e-04 6.122939e-02
#4   9.039240e-01 1.524734e-02 9.191714e-01
#5   1.261741e-02 2.087247e-04 1.282613e-02
#Sum 9.840013e-01 1.599875e-02 1.000000e+00

library("gmodels")
CrossTable(song_data$explicit,song_data$HOT100,chisq = T,fisher = T,format = "SAS")
#p = 4.989376e-145
CrossTable(song_data$key,song_data$HOT100,chisq = T,fisher = T,format = "SAS")
#p= 1.616158e-08 
CrossTable(song_data$mode,song_data$HOT100,chisq = T,fisher = T,format = "SAS")
#p = 8.213556e-05 
CrossTable(song_data$time_signature,song_data$HOT100,chisq = T,fisher = T,format = "SAS")
#p = 3.894329e-06
#In addition: Warning message:
#In chisq.test(t, correct = FALSE, ...) : Chi-squared 近似演算法有可能不準

###Continious variables###

# 1. popularity: p-value(2e-16) < 0.05 => 有相關
qplot(factor(HOT100,levels = c(0,1),labels = c('False','True')), popularity,data = song_data,geom = "boxplot",xlab = "HOT100_True/False")
popularity = aov(D$HOT100~D$popularity)
summary(popularity)

# 2. danceability: p-value(1.92e-08) < 0.05 => 有相關
qplot(factor(HOT100,levels = c(0,1),labels = c('False','True')), danceability,data = song_data,geom = "boxplot",xlab = "HOT100_True/False")
danceability = aov(song_data$HOT100~song_data$danceability)
summary(danceability)

# 3. energy:  p-value(0.000554) < 0.05 => 有相關
qplot(factor(HOT100,levels = c(0,1),labels = c('False','True')), energy,data = song_data,geom = "boxplot",xlab = "HOT100_True/False")
energy = aov(song_data$HOT100~song_data$energy)
summary(energy)

# 4. acousticness: p-value(2e-16) < 0.05 => 有相關
qplot(factor(HOT100,levels = c(0,1),labels = c('False','True')), acousticness,data = song_data,geom = "boxplot",xlab = "HOT100_True/False")
acousticness = aov(song_data$HOT100~song_data$acousticness)
summary(acousticness)
