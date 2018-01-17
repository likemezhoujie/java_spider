package com.yida.spider.huxiu.thread;

import com.yida.spider.huxiu.HuxiuSpiderThreadPool;
import com.yida.spider.huxiu.Dao.ArticleDao;
import com.yida.spider.huxiu.pojo.Article;

public class SaveArticleRunnable implements Runnable{
		


	@Override
	public void run() {
		
		while(true){
			try {
				// 从articleContentQueue队列取出article对象放到数据库中
				Article article = HuxiuSpiderThreadPool.articleContentQueue.take();
				HuxiuSpiderThreadPool.articleDao.save(article);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	

}
