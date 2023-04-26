#randomforest
setwd("/")

#install.packages('smotefamily')
library(dplyr);library(ggplot2);library(smotefamily)
df <- read.csv("final_3.csv", sep = ",")
df <- df[,-c(1:3,6:8,21:25,27)]
freqdf <- as.data.frame(table(df$HOT100))
bp <- barplot(freqdf$Freq, names.arg = freqdf$Var1)
text(bp, freqdf$Freq , labels=freqdf$Freq, xpd=TRUE)

#Train/Test
set.seed(123)
train_idx <- sample(1:nrow(df), nrow(df) * 0.7)
train <- df[train_idx, ]
test <- df[setdiff(1:nrow(df), train_idx),] 

#SMOTE
str(train)
train_n<- as.data.frame(sapply(train, as.numeric))
train_smote = SMOTE(train_n[-18],train_n$HOT100)
train_smote = train_smote$data
freqftrain <- as.data.frame(table(train_smote$HOT100))
bp <- barplot(freqftrain$Freq, names.arg = freqftrain$Var1)
text(bp, freqftrain$Freq , labels=freqftrain$Freq, xpd=TRUE)

#--------------------------------------------------------------------------------
#install.packages('skimr')


## packages for builiding models
library(caret)  # for workflow
library(tidyverse)
library(skimr)  # neat alternative to glance + summary

train_df <- train_smote[,-c(16)]
train_df$HOT100 = as.factor(train_df$HOT100)
#train_df %>% 
  #mutate(HOT100 = factor(HOT100, 
                        #labels = make.names(levels(HOT100))))
levels(train_df$HOT100)=c("No","Yes")

### randomForest ===> don't use this
library(randomForest)
rf.final <- randomForest (HOT100 ~ ., data = train_df ,
                                  #subset = index.train , 
                                  nodesize=c(1,2,3,4,5),
                                  mtry = 3 ,      
                                  ntree=800 ,
                                  importance = TRUE)
rf.final
importance (rf.final) 
varImpPlot (rf.final)
plot(rf.final)
###

### caret 1 ===> don't use this
df.frst = train(HOT100 ~ ., 
                data = train_df, 
                method = "ranger",  # for random forest
                tuneLength = 5,  # choose up to 5 combinations of tuning parameters
                metric = "ROC",  # evaluate hyperparamter combinations with ROC
                trControl = trainControl(
                  method = "cv",  # k-fold cross validation
                  number = 10,  # 10 folds
                  savePredictions = "final",       # save predictions for the optimal tuning parameter1
                  classProbs = TRUE,  # return class probabilities in addition to predicted values
                  summaryFunction = twoClassSummary  # for binary response variable
                )
)
#Warning message:
  #In train.default(x, y, weights = w, ...) :
  #The metric "Accuracy" was not in the result set. ROC will be used instead.
df.frst

plot(df.frst)
###



### caret 2 ===> use this
ctrl <- trainControl(method = "cv", classProbs = TRUE, summaryFunction = twoClassSummary, number = 10, savePredictions = "final")
set.seed(123)
trained3 <- train(HOT100 ~ . , 
                  data = train_df, 
                  method = "rf", 
                  ntree = 800, 
                  tunelength = 10, 
                  metric = "ROC", 
                  trControl = ctrl, 
                  importance = TRUE)

trained3
plot(trained3)
###

test$HOT100 = as.factor(test$HOT100)
#test %>% mutate(HOT100 = factor(HOT100, labels = make.names(levels(HOT100))))
levels(test$HOT100)=c("No","Yes")

df.pred <- predict(trained3, test)
df.pred.prob <- predict(trained3, test, type="prob")[,2]

df.pred
df.pred.prob

#table(observed=test$HOT100 , predicted = df.pred.prob) # Confusion matrix
mean(test$HOT100==df.pred) # Accuracy

plot(test$HOT100, df.pred.prob, 
     main = "Random Forest Classification: Predicted vs. Actual",
     xlab = "Actual",
     ylab = "Predicted")

plot(df.pred.prob)
#df.conf <- confusionMatrix(data = df.pred.prob, 
                            #reference = test$HOT100)
#df.conf
caret::RMSE(as.numeric(df.pred.prob) , as.numeric(test$HOT100))

#AUC/ROC
library(pROC)
roc_pred_smote = roc(test$HOT100,as.numeric(df.pred.prob))#Setting levels
# ROC for 3 cutoffs
plot.roc(roc_pred_smote, print.thres = c(0.8, 0.5, 0.1), print.auc=T)
plot.roc(roc_pred_smote, print.thres = "best", 
         print.thres.best.method="youden", print.auc = T)#0.519

