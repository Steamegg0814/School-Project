
library(rvest);library(magrittr);library(stringr);library(data.table)

start_date <- as.Date('2010-01-01')
end_date <- as.Date('2019-12-31')
all_dates <- seq(start_date, end_date, by = 'day')
dates <- all_dates[weekdays(all_dates) %in% c("星期六")]
url <- paste0("https://www.billboard.com/charts/hot-100/",dates)


getdata <- function(url){
  html <- tryCatch(read_html(url), error=function(e) NULL)
  rows <- html %>% html_elements(".chart-results-list .o-chart-results-list-row")

    # scrape data
    rank <- rows %>% 
      xml_find_all("//span[contains(@class, 'c-label  a-font-primary-bold-l u-font-size-32@tablet u-letter-spacing-0080@tablet')]") %>% 
      html_text() 

    
    artist <- rows %>% 
      xml_find_all("//span[contains(@class, 'c-label  a-no-trucate a-font-primary-s lrv-u-font-size-14@mobile-max u-line-height-normal@mobile-max u-letter-spacing-0021 lrv-u-display-block a-truncate-ellipsis-2line u-max-width-330 u-max-width-230@tablet-only')]") %>% 
      html_text()
    
    song <- rows %>% 
      html_elements(".c-title") %>% 
      html_text(trim = T)
    
    
    lastweek<- rows %>%
      html_nodes(xpath='//ul/li[4]/ul/li[7]/ul/li[3]') %>% 
      html_text()
    
    peakpos <- rows %>% 
      html_nodes(xpath='//ul/li[4]/ul/li[7]/ul/li[4]') %>% 
      html_text()
    
    weekson <- rows %>%
      html_nodes(xpath='//ul/li[4]/ul/li[7]/ul/li[5]') %>% 
      html_text()

    date <- gsub("https://www.billboard.com/charts/hot-100/", "", url)
    
    #彙總dataset
    df <- data.frame(date, rank, song, artist, lastweek, peakpos, weekson)
}

data <- rbindlist(parallel::mclapply(url, getdata))
data

#匯出
write.csv(data,file="Dataset/billboard.csv",row.names = FALSE)

