
library(readr);library(purrr);library(ggplot2);library(dplyr);library(stringr)

final <- data.frame(read_csv("Dataset/final_3.csv"))
final$time_signature<-as.factor(final$time_signature)
final$mode<-as.factor(final$mode)
final$key<-as.factor(final$key)
final$explicit<-as.factor(final$explicit)
final$HOT100<-as.factor(final$HOT100)


uni_column<-c("acousticness","instrumentalness","liveness","valence","tempo","duration_ms","time_signature","popularity",
              "explicit","danceability","energy","key","loudness","mode","speechiness","HOT100")
uni <- function(col_name){
  if(is.factor(final[ ,col_name])){
    if(col_name == "HOT100"){
      #圓餅圖 for billboard
      type <- c("not in Billboardl"," in Billboard")
      nums <- table(final[,col_name])
      nums<-as.numeric(nums)
      
      #nums<-c(2872,17095)
      df<-data.frame(type=type,nums=nums)
      #繪製條形圖 
      p <- ggplot(data =df , mapping = aes(x = 'Content', y = nums, fill = type)) + geom_bar(stat = 'identity', position = 'stack',width = 1)
      label_value <- paste('(', round((table(final[,col_name])/nrow(final)*100), 8), '%)', sep = '') 
      label <- paste(type, label_value, sep = '') 
      p + coord_polar(theta = 'y') + labs(x = '', y = '', title = 'Billboard',fill=col_name) + 
        theme(axis.text = element_blank()) + theme(axis.ticks = element_blank()) +
        scale_fill_discrete(labels = label)
    }else{
      #bar
      pic_bar <- ggplot(data=final,aes(x=final[,col_name] ,fill=final[ ,col_name])) +
        geom_bar() +
        labs(x = col_name,fill = col_name )
      print(pic_bar)
      print(col_name)
      #圓餅圖
      
      type<-levels(final[ ,col_name])
      nums <- table(final[ ,col_name])
      nums<-as.numeric(nums)
      #nums<-c(2872,17095)
      df<-data.frame(type=type,nums=nums)
      #繪製條形圖 
      p <- ggplot(data =df , mapping = aes(x = 'Content', y = nums, fill = type)) + geom_bar(stat = 'identity', position = 'stack',width = 1)
      label_value <- paste('(', round((table(final[ ,col_name])/nrow(final)*100), 2), '%)', sep = '') 
      label <- paste(type, label_value, sep = '') 
      p + coord_polar(theta = 'y') + labs(x = '', y = '', title = col_name,fill=col_name) + 
        theme(axis.text = element_blank()) + theme(axis.ticks = element_blank()) +
        scale_fill_discrete(labels = label)
    }
    
    
  }
  else{#連續型變數
    #pic_density <- ggplot(data=final , aes(x=final[,col_name],fill=final[,"HOT100"]) ) +
    #  geom_density(alpha=0.4) +
    #  labs(x = col_name,fill="HOT100")
    pic_density <- ggplot(data=final , aes(x=final[,col_name],y = ..scaled..) ) +
        geom_density(fill ="gray") +
        labs(x = col_name, y = "density") + 
        scale_x_continuous(labels = scales::comma)
    print(pic_density)
    pic_box <- ggplot(data=final , aes(y=final[,col_name])) + geom_boxplot() + scale_x_discrete() + 
                      ylab(col_name) + 
              scale_y_continuous(labels = scales::comma)
    
    print(pic_box)
    t<-nortest::lillie.test(final[,col_name])
    cat(col_name,"\n")
    print(t)
    #age_rnorm<-rnorm(517307,mean=mean(final[,col_name]),sd=sd(final[,col_name]))
    #ks<-ks.test(x=final[,col_name],y=age_rnorm,exact = TRUE)# p-value = 0.6389 沒拒絕H0(無明顯差距)+D值愈小->符合常態分布
    
    
    

    
  }
}

sapply(uni_column , uni)#sapply return vector

#duration range 太大，取log10
final %>% ggplot(aes(x = duration_ms, y = ..scaled..))+
  geom_density(fill ="gray") +
  labs(x = "log scale duration_ms" , y = "density") + 
  scale_x_continuous(trans = "log10", labels = scales::comma)


final %>% ggplot(aes(y=duration_ms)) + geom_boxplot() + scale_x_discrete() + 
  ylab("log scale duration_ms") + 
  scale_y_continuous(trans = "log10" ,labels = scales::comma)
