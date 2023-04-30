library(tm)
library(dplyr)
library(tidyverse)
library(tidytext)
library(stringr)
library(purrr)
library(reshape2)
library(wordcloud)
library(RColorBrewer)
library(jiebaR)
library(tmcn)
library(wordcloud2)
library(showtext)
showtext_auto(enable = TRUE)
font_add("jf-openhuninn", "C:/Users/Bomb/Desktop/Course/碩一上/Fintech/Untitled Folder/jf-openhuninn-1.1.ttf")
DataDF <- read.csv(file='C:/Users/Bomb/Desktop/comment.csv',fileEncoding="UTF-8",header=TRUE, stringsAsFactors=FALSE)
colnames(DataDF) <- c("index","line","text","time")
DataDF$time <- as.Date(DataDF$time)
#DataDF$text <- gsub("[^0-9A-Za-z///' ]","'" , DataDF$text ,ignore.case = TRUE)
str_replace_all(DataDF$text, "[^[:alnum:]]", " ")
#刪除特殊符號
DataDF$text <- gsub("[^a-zA-Z0-9\u4e00-\u9fa5]","",DataDF$text)
#刪除數字
DataDF$text <- gsub("\\d","",DataDF$text)
#刪除空值
DataDF <- na.omit(DataDF)

#------------- get_noun ----------------------
get_noun = function(x){
  stopifnot(inherits(x,"character"))
  index = names(x) %in% c("n","nr","nr1","nr2","nrj","nrf","ns","nsf","nt","nz","nl","ng","ad","an","ag","al")
  x[index]
}
#---------------tokenizer--------------------
chi_tokenizer <- function (t) {
  lapply(t, function(x){
    tokens <- segment(x, jieba_tokenizer)
    tokens <- get_noun(tokens[nchar(tokens) > 1])
    #tokens <- tokens[nchar(tokens) > 1]
    return(tokens)
  })
}
#---------------More than Five --------------------SS
IsMoreThanFive <- function(x) {
  ifelse(x > 5, 5, ifelse(x <= -5, -5, x))
}  

#---------------user define stop words ------
swords <- stopwords::stopwords("zh", source = "stopwords-iso")
swords <- c(swords,"嗎","xd","幹","股市","股價","疫情")
stopwords <- data_frame(word=swords)
setwd("C:/Users/Bomb/Desktop")
#-----------------------------------------------------------

# --------------------Jieba TAG and User Define Dict -----
#jieba_tokenizer = worker(type="tag", user = "three_kingdoms_lexicon.traditional.dict")
#jieba_tokenizer = worker(user = "three_kingdoms_lexicon.traditional.dict")
jieba_tokenizer = worker("tag")
#jieba_tokenizer = worker()
#----------------   unnest -------------------------------
tokens <- DataDF %>%
  unnest_tokens(word, text, token = chi_tokenizer) %>%
  anti_join(stopwords)
#tokens_clean <-tokens[-grep("\\b\\d+\\b", tokens$word),]
#--------- ???r?? ----------------------
tokens_count <- tokens %>% 
  anti_join(stopwords) %>%
  #filter(nchar(.$word) > 1) %>%
  group_by(word) %>% 
  summarise(sum = n()) %>% 
  filter(sum > 50) %>%
  arrange(desc(sum))
wordcloud2(tokens_count)

#-----------  ?ۦ??ק? setwd() ---Ū??AFINN-111.txt ?ؿ?
afinn_list <- read.delim(file='C:/Users/Bomb/Desktop/AFINN-111_v3.txt',fileEncoding="UTF-8",header=FALSE, stringsAsFactors=FALSE)
names(afinn_list) <- c('word', 'score')
afinn_list$word <- tolower(afinn_list$word)

#------------------ sentiment Analysis -------------------------------
text_line       <- tokens$line
text_time    <- tokens$time
text_word       <- tokens$word
text_dataset <- data_frame(time = text_time,line=text_line,word=text_word)
#---------------------------calaulate score of sentiment ---------------------
afinn_score_df <- text_dataset %>% inner_join(afinn_list,by="word")  %>% group_by(time)  %>% arrange(time)
#afinn_score_df <- text_dataset %>% inner_join(afinn_list,by="word")  %>% group_by(time)  %>% arrange(time)
sentiment_score_df <- afinn_score_df %>% group_by(time)  %>% arrange(time) %>% summarise(sentiment=mean(as.numeric(score)) )
sentiment_score_df$sentiment <- IsMoreThanFive(sentiment_score_df$sentiment)
sentiment_score_df %>% summarise(sscore =mean(sentiment))
positive_lines_df <- sentiment_score_df %>%  filter(sentiment > 0) %>% left_join(text_dataset,by="time")
negative_lines_df <- sentiment_score_df %>% filter(sentiment < 0) %>% left_join(text_dataset,by="time")
sentiment_words_df <- rbind(positive_lines_df,negative_lines_df)
sentiment_words_polarity_df <- mutate(sentiment_words_df,polarity=ifelse(sentiment >0,"positive","negative"))
#-----------WorldCound Generator
sentiment_words_polarity_df %>% 
  anti_join(stopwords) %>%
  count(word,polarity, sort=TRUE) %>%
  acast(word ~ polarity, value.var = "n", fill = 0) %>%
  comparison.cloud(colors=  c("indianred3","lightsteelblue3"), max.words = 250)
wordcloud2(sentiment_words_polarity_df)
#commonality.cloud(random.order=FALSE, scale=c(5, .5),colors = brewer.pal(4, "Dark2"), max.words=30)
#---------------------------calaulate score of sentiment ---------------------
#------------------ sentiment Analysis -------------------------------
postive_token_count <- positive_lines_df %>% 
  anti_join(stop_words) %>%
  count(word) %>% 
  filter(n > 60) %>%
  arrange(desc(n))
wordcloud2(postive_token_count)


negative_token_count <- negative_lines_df %>% 
  anti_join(stopwords) %>%
  count(word) %>% 
  filter(n > 60) %>%
  arrange(desc(n))
wordcloud2(negative_token_count)

write.csv(sentiment_score_df, "sentiment.csv", row.names = FALSE)

stock <- read.csv(file='C:/Users/Bomb/Desktop/Course/碩一上/Fintech/Untitled Folder/文字雲/sentiment.csv',fileEncoding="UTF-8",header=TRUE, stringsAsFactors=FALSE)
sentiment <- read.csv(file='C:/Users/Bomb/Desktop/Course/碩一上/Fintech/Untitled Folder/comment.csv',fileEncoding="UTF-8",header=TRUE, stringsAsFactors=FALSE)

merge <- merge(x = stock, y = sentiment,all.x = TRUE)
write.csv(merge, "stock_sentiment.csv", row.names = FALSE)
