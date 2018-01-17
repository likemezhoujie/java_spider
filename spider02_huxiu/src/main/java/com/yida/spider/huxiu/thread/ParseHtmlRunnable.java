package com.yida.spider.huxiu.thread;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.yida.spider.huxiu.HuxiuSpiderThreadPool;
import com.yida.spider.huxiu.pojo.Article;

public class ParseHtmlRunnable implements Runnable{
	@Override
	public void run() {
		while(true){
			parseSingleHtml();
		}
	}

	private void parseSingleHtml() {
		try {
			//从articleHtmlQueue队列里得到html文档
			String html = HuxiuSpiderThreadPool.articleHtmlQueue.take();
			//准备封装article的对象
			Article article = new Article();
			//解析html
			Document document = Jsoup.parse(html);
			
			//解析文章的id
			String id = document.select("ul[data-id]").get(0).attr("data-id");
			article.setId(id);
			
			//解析文章的url(直接拼接就可以了)
			String url = "https://www.huxiu.com/article/"+id+".html";
			article.setUrl(url);
			
			//解析文章的title
			String title = document.select("title").get(0).text();
			article.setTitle(title);
			System.out.println(title);
			
			// 解析文章author author-name
			Elements names = document.select(".author-name");
			String name = names.get(0).text();
			article.setAuthor(name);
			
			// 解析文章发布时间
			Elements dates = document.select("[class^=article-time]");
			String date = dates.get(0).text();
			article.setCreateTime(date);
			
			// 解析文章 评论数
			Elements pls = document.select("[class^=article-pl]");
			String pl = pls.get(0).text();
			article.setPl(pl);
			
			// 解析文章 点赞数 num
			Elements nums = document.select(".num");
			String num = nums.get(0).text();
			article.setZan(num);
			
			// 解析文章 收藏数
			Elements shares = document.select("[class^=article-share]");
			String share = shares.get(0).text();
			article.setSc(share);
			
			// 解析文章正文内容 article-content-wrap
			Elements content = document.select(".article-content-wrap p");
			String contentText = content.text();
			article.setContent(contentText);
			
			//解析完了后吧article对象放到articleContentQueue队列中
			HuxiuSpiderThreadPool.articleContentQueue.put(article);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
