library(dplyr);library(stringr);library(tidyr);library(data.table)
# 讀取檔案
spotify = read.csv("Dataset/tracks.csv")
billiboard = read.csv("Dataset/billboard.csv")

# 抓取2010-01-01 ~ 2020-12-18
spotify_filter = spotify %>% filter(release_date >= "2010-01-01" & release_date >= "2019-12-31")
billiboard_filter = billiboard %>% filter(date >= "2010-01-01" & date <= "2019-12-31")
billiboard_filternew= billiboardnew %>% filter(date >= "2010-01-01" & date <= "2019-12-31")
#spotify、billiboard 資料清理
#spotify_filter$artists <- lapply(spotify_filter$artists, function (x) {gsub(fixed = TRUE,"[","",gsub(fixed = TRUE,"'","",gsub(fixed = TRUE,"']","",x)))})

#thepattern = paste0( c("- Live", "  [Live]", "(live)"), collapse = "|")
#spotify_filter<-spotify_filter[!grepl(thepattern , spotify_filter$name),] 
#spotify_filter$name = str_replace(spotify_filter$name, pattern = " - Instrumental", replacement = "")

# 10/30新增的
spotify_filter$artists <- lapply(spotify_filter$artists, function (x) {gsub(fixed = TRUE,"[","",gsub(fixed = TRUE,"'","",gsub(fixed = TRUE,'"',"",gsub(fixed = TRUE,"]","",x))))})
spotify_filter$artists = str_replace_all(spotify_filter$artists, pattern = ", ", replacement = ",")
thepattern = paste0( c("- Live", "  [Live]", "(live)"), collapse = "|")
spotify_filter<-spotify_filter[!grepl(thepattern , spotify_filter$name),]
spotify_filter$name = str_replace(spotify_filter$name, pattern = " - Instrumental", replacement = "")
spotify_filter$artists = as.character(spotify_filter$artists)
spotify_filter$artists = sapply(strsplit(spotify_filter$artists, ","), function(x) paste(sort(x), collapse=","))

# billiboard artist整理
billiboard_filter$artist = str_replace(billiboard_filter$artist, pattern = " Featuring ", replacement = ",")
billiboard_filter$artist = str_replace(billiboard_filter$artist, pattern = " & ", replacement = ",")
billiboard_filter$artist = str_replace(billiboard_filter$artist, pattern = " x ", replacement = ",")
billiboard_filter$artist = str_replace(billiboard_filter$artist, pattern = " X ", replacement = ",")
billiboard_filter$artist = str_replace_all(billiboard_filter$artist, pattern = ", ", replacement = ",")
billiboard_filter$artist = sapply(strsplit(billiboard_filter$artist, ","), function(x) paste(sort(x), collapse=","))

# billiboard、spotify artist分割
#spotify_filter = separate(spotify_filter, artists, c("artist_1", "artist_2", "artist_3", "artist_4", "artist_5"), ",")
#billiboard_filter = separate(billiboard_filter, artist, c("artist_1", "artist_2", "artist_3", "artist_4", "artist_5"), ",")

# 兩者join 歌名與歌手要一致才join，加入HOT100，
combine_join = left_join(spotify_filter, billiboard_filter, by = c("name" = "song", "artists" = "artist")) # 將歌名與歌手相符的資料合併
combine_join_filter = combine_join %>% mutate(HOT100 = case_when(is.na(peak.rank)~0, TRUE ~ 1)) %>% mutate(best = (weeks.on.board - peak.rank)) %>% group_by(name, artists) %>% arrange(desc(best)) %>% slice(1)
#combine_join_filter = combine_join_filter %>% filter(HOT100 == 1)

#library(sqldf)
#combine_join = combine_join %>% filter(!is.na(rank)) # 有進百大的
#combine = sqldf("SELECT * FROM spotify_filter LEFT JOIN billiboard_filter ON (spotify_filter.name = billiboard_filter.song) WHERE billiboard.artist = spotify.artists")

# 匯出檔案
write.csv(combine_join_filter,file="Dataset/final_3.csv",row.names = FALSE)
 