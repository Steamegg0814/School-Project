library(dplyr);library(ggplot2);library(smotefamily);library(caret);library(xgboost);library(Matrix)


#load
df <- read.csv("/", sep = ",")
#去除欄位
df <- df[,-c(1:2,6:8,21:25,27)]
freqdf <- as.data.frame(table(df$HOT100))
#freq table

#Train/Test
set.seed(123)
train_idx <- sample(1:nrow(df), nrow(df) * 0.7)
train <- df[train_idx, ]
test <- df[setdiff(1:nrow(df), train_idx),] 

#SMOTE
train_n<- as.data.frame(sapply(train, as.numeric))
train_smote = SMOTE(train_n[-18],train_n$HOT100)
train_smote = train_smote$data
freqftrain <- as.data.frame(table(train_smote$HOT100))
bp <- barplot(freqftrain$Freq, names.arg = freqftrain$Var1)
text(bp, freqftrain$Freq , labels=freqftrain$Freq, xpd=TRUE)

#xgb
train_smote <- train_smote[,-c(1,6,14,17)]
test <- test[,-c(1:2,6,14)]
#data
#data_to_metrix
train_matrix <- sparse.model.matrix(HOT100 ~ .-1, data = train_smote)
test_matrix <- sparse.model.matrix(HOT100 ~ .-1, data = test)
#label
train_label <- as.numeric(train_smote$HOT100)
test_label <-  as.numeric(test$HOT100)
#to list
train_list <- list(data=train_matrix,label=train_label) 
test_list <- list(data=test_matrix,label=test_label) 
#to xgb matrix
dtrain <- xgb.DMatrix(data = train_list$data, label = train_list$label)
dtest <- xgb.DMatrix(data = test_list$data, label = test_list$label)

param <- list("objective" = "binary:logistic",     
              "eval_metric" = "rmse", 
              "gamma" = 0,    
              "colsample_bytree" = 1,   
              "min_child_weight" = 12,
              "early_stopping_rounds" = 10
)

nround.cv = 300
system.time(bst_cv <- xgb.cv(param=param, data=dtrain , 
                             nfold=10, nrounds=nround.cv, prediction=TRUE, verbose=TRUE))

#找RMSE最小的index
min_RMSE_idx = which.min(bst_cv$evaluation_log[, test_rmse_mean]) #300
bst_cv$evaluation_log[min_RMSE_idx,]

#best model
# moding training
system.time( bst <- xgboost(param=param, data=dtrain, 
                            nrounds=min_RMSE_idx, verbose=0))

#cm
pred = predict(bst,newdata = dtest)
pred_class <- as.numeric(pred > 0.5)
cm <- confusionMatrix(as.factor(pred_class), as.factor(test_label))
plt <- as.data.frame(cm$table)
#plot confusion matrix
ggplot(plt, aes(Prediction,Reference, fill= Freq)) +
  geom_tile() + geom_text(aes(label=Freq)) +
  scale_fill_gradient(low="white", high="#D4B8B4") +
  labs(x = "Reference",y = "Prediction") +
  scale_x_discrete(labels=c("0","1")) +
  scale_y_discrete(labels=c("0","1"))

#importance
# dump
model = xgb.dump(bst, with_stats = TRUE)
# get the feature real names
names = dimnames(dtrain)[[2]]
# compute feature importance matrix
importance_matrix = xgb.importance(names, model=bst)
# plot
im_plt <- xgb.ggplot.importance(importance_matrix,n_clusters = 1)
im_plt + theme_bw(base_size = 18)


#predict
xgboost_roc <- roc(test_label,as.numeric(pred))
plot(xgboost_roc, print.auc=TRUE, auc.polygon=TRUE, 
     print.thres=TRUE,legacy.axes = TRUE,main='ROC curve')