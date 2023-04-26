library(dplyr)
library(smotefamily)
library(caret)
library(pROC)
library(Metrics)
library(epiDisplay)
library(boot)
df <- read.csv("/", sep = ",")
df <- df[,-c(1:4,6:8,11,19,21:25,27)]
as.data.frame(table(df$HOT100))


# Train/Test
set.seed(123)
train_idx <- sample(1:nrow(df), nrow(df) * 0.7)
train <- df[train_idx, ]
test <- df[-train_idx, ] 

# SMOTE
str(train) 
train_n<- as.data.frame(sapply(train, as.numeric))
smote = SMOTE(train_n[-18],train_n$HOT100)
smote = smote$data
smote$HOT100 = as.factor(smote$HOT100)
as.data.frame(table(smote$HOT100))


# cross validation glm
train_control = trainControl(method = "cv", 
                             number = 10)

set.seed(123)
#model_train = train(HOT100 ~ explicit+danceability+energy+loudness+mode+speechiness+acousticness+instrumentalness+liveness+valence+time_signature, data=smote, method = "glm",
#                    family = binomial, trControl = train_control)
model_train = glm(HOT100 ~ explicit+danceability+energy+loudness+mode+speechiness+acousticness+instrumentalness+liveness+valence+time_signature, data = smote, 
                 family = "binomial")

print(model_train)

# Logistic display
logistic.display(model_train)

# Variable importance
varimp = varImp(model_train)
varimp = varimp[order(varimp$Overall, decreasing = T),, drop = F]
varimp
ggplot2::ggplot(varimp, aes(x=reorder(rownames(varimp),Overall), y=Overall)) +
  geom_bar(stat = "identity")+
  xlab('Variable')+
  ylab('Importance')+
  theme_light() +
  coord_flip()

# Prediction & RMSE
test_result = predict(model_train, newdata = test)
test_result = ifelse(test_result > 0.5,1,0)
head(test_result)
rmse(test$HOT100, test_result) 

# ConfusionMatrix 
confusionMatrix(data=as.factor(test_result),reference=as.factor(test$HOT100))

# ROC/AUC
test_prob = predict(model_train, newdata = test, type = "response")
par(pty = "s") #指定畫布繪製成正方形的大小
test_roc = roc(test$HOT100 ~ test_prob, plot = TRUE,
               print.auc = TRUE,legacy.axes=TRUE, auc.polygon = TRUE, print.thres = TRUE) 


