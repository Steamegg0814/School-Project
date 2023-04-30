#爬蟲
from bs4 import BeautifulSoup
import requests
import json
import pandas as pd

# 基本參數
url = "https://www.ptt.cc/bbs/Stock/search?q=%E7%96%AB%E6%83%85"
payload = {
    'from': '/bbs/Stock/search?q=%E7%96%AB%E6%83%85',
    'yes': 'yes'
}
data = []   # 全部文章的資料
num = 0
rs = requests.session()
#未滿18歲
response = rs.post("https://www.ptt.cc/ask/over18", data=payload)

# 爬取兩頁
for i in range(54):
    # get取得頁面的HTML
    # print(url)
    response = rs.get(url)
    soup = BeautifulSoup(response.text, "html.parser")
    # print(soup.prettify())

    # 找出每篇文章的連結
    links = soup.find_all("div", class_="title")
    for link in links:
        # 如果文章已被刪除，連結為None
        if link.a != None:

            article_data = {}   # 單篇文章的資料
            page_url = "https://www.ptt.cc/"+link.a["href"]

            # 進入文章頁面
            response = rs.get(page_url)
            result = BeautifulSoup(response.text, "html.parser")
            # print(soup.prettify())

            # 找出作者、標題、時間、留言
            main_content = result.find("div", id="main-content")
            article_info = main_content.find_all(
                "span", class_="article-meta-value")

            if len(article_info) != 0:
                author = article_info[0].string  # 作者
                title = article_info[2].string  # 標題
                time = article_info[3].string   # 時間
            else:
                author = "無"  # 作者
                title = "無"  # 標題
                time = "無"   # 時間

            #print(author)
            #print(title)
            #print(time)

            article_data["author"] = author
            article_data["title"] = title
            article_data["time"] = time

            # 將整段文字內容抓出來
            all_text = main_content.text
            # 以--切割，抓最後一個--前的所有內容
            pre_texts = all_text.split("--")[:-1]
            # 將前面的所有內容合併成一個
            one_text = "--".join(pre_texts)
            # 以\n切割，第一行標題不要
            texts = one_text.split("\n")[1:]
            # 將每一行合併
            content = "\n".join(texts)

            # print(content)
            article_data["content"] = content

            # 一種留言一個列表
            comment_dic = {}
            push_dic = []
            arrow_dic = []
            shu_dic = []

            # 抓出所有留言
            comments = main_content.find_all("div", class_="push")
            for comment in comments:
                push_tag = comment.find(
                    "span", class_="push-tag").string   
                push_userid = comment.find(
                    "span", class_="push-userid").string  
                push_content = comment.find(
                    "span", class_="push-content").string   
                push_time = comment.find(
                    "span", class_="push-ipdatetime").string 

                comment_dict = {"push_userid": push_userid,
                         "push_content": push_content, "push_time": push_time}
                if push_tag == "推 ":
                    push_dic.append(comment_dict)
                if push_tag == "→ ":
                    arrow_dic.append(comment_dict)
                if push_tag == "噓 ":
                    shu_dic.append(comment_dict)

            comment_dic["推"] = push_dic
            comment_dic["→"] = arrow_dic
            comment_dic["噓"] = shu_dic
            article_data["comment"] = comment_dic

            # print(article_data)
            data.append(article_data)
    # 找到上頁的網址，並更新url
    url = "https://www.ptt.cc/"+soup.find("a", string="‹ 上頁")["href"]
df = pd.DataFrame(data)
len(df)

#處理時間
df['time'] = pd.to_datetime(df['time']).dt.date
#df.to_csv('pttscrawler.csv',encoding='utf-8')
df['comment'].tail()
df = pd.DataFrame.from_dict({"article_date":article_date,"article_title":article_title,"article_author":article_author,"article_content":article_content,
                  "comment":article_messages}, orient='index')
df = df.transpose()
df['article_date'] = pd.to_datetime(df['article_date']).dt.date
import json
# 透過force_ascii=False 解決中文的編碼問題
js = df.to_json("pttcrawler.json",orient = 'records', force_ascii=False) # date type = str
df.to_csv("pttcrawlernew.csv",encoding='utf_8_sig')

df = pd.DataFrame({"article_date":article_date,"article_title":article_title,"article_author":article_author,'comment_content':comment_content,'comment_time':comment_time})
df