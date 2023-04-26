library(smotefamily);library(caret);library(pROC);library(Metrics);library(epiDisplay);library(rpart);library(rpart.plot);library(dplyr)

# Original dataset
df <- read.csv("/", sep = ",")
df <- df[,-c(1,2,6:8,11,19,21:25,27)]
as.data.frame(table(df$HOT100))


# Train/Test
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

# Control
train_control = trainControl(method = "cv", 
                             number = 10)

cartGrid <- expand.grid(.cp=(1:50)*0.01)


# model, use cross-validation, choose the best cp
set.seed(123)
cart.model = train(HOT100 ~ popularity+explicit+danceability+energy+loudness+mode+speechiness+acousticness+instrumentalness+liveness+valence+time_signature,
                   data = smote, method = "rpart", trControl = train_control, tuneGrid = cartGrid)

print(cart.model)
# Acc:0.7265264 Kappa:0.4533789 => cp 0.01


# Tree
main_tree <- rpart(HOT100 ~ popularity+explicit+danceability+energy+loudness+mode+speechiness+acousticness+instrumentalness+liveness+valence+time_signature, 
                   data = smote, control = rpart.control(cp=0.5))

prp(main_tree,         # 模型
    faclen=0,           # 呈現的變數不要縮寫
    fallen.leaves=TRUE, # 讓樹枝以垂直方式呈現
    shadow.col="gray",  # 最下面的節點塗上陰影
    # number of correct classifications / number of observations in that node
    extra=2)  


# Variable importance
varimp = as.data.frame(main_tree$variable.importance)
colnames(varimp) = c("Importance")
varimp
ggplot2::ggplot(varimp, aes(x=reorder(rownames(varimp),Importance), y=Importance)) +
  geom_bar(stat = "identity")+
  xlab('Variable')+
  ylab('Importance')+
  theme_light() +
  coord_flip()

# 決策樹規則
rpart.rules(x = main_tree,cover = TRUE)

# Prediction & RMSE
test_result = predict(main_tree, newdata = test, type = "prob")
colnames(test_result) = c("No", "Yes")
test_result = data.frame(test_result)
test_result = ifelse(test_result$Yes > 0.5,1,0)

rmse(test$HOT100, test_result)


# ConfusionMatrix 
confusionMatrix(data=as.factor(test_result),reference=as.factor(test$HOT100))

# ROC/AUC
head(test_result)
model_roc = roc(test$HOT100, test_result)
print(model_roc)

test_prob = predict(main_tree, newdata = test, type = "prob")

par(pty = "s") #指定畫布繪製成正方形的大小
test_roc = roc(test$HOT100 ~ test_prob[,2], plot = TRUE,
               print.auc = TRUE,legacy.axes=TRUE, auc.polygon = TRUE, print.thres = TRUE) 


