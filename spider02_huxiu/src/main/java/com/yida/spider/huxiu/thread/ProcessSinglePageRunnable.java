package com.yida.spider.huxiu.thread;

import org.apache.http.client.methods.HttpGet;

import com.yida.spider.huxiu.HuxiuSpiderThreadPool;

public class ProcessSinglePageRunnable implements Runnable{

	@Override
	public void run() {
		
		while(true){
			try {
				//解析每个url
				processSingleUrl();
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 解析每个url
	 * @author zhoujie
	 */
	private void processSingleUrl() {
		try {
			String aid = HuxiuSpiderThreadPool.urlQueue.take();
			//得到每个url
			String url = "https://www.huxiu.com/article/"+aid+".html";
			//创建get请求
			HttpGet httpGet = new HttpGet(url);
			//解析url得到html文档
			String htmlByRequest = HuxiuSpiderThreadPool.getHtmlByRequest(httpGet);
			//将得到的html文档加入到articleHtmlQueue队列中
			HuxiuSpiderThreadPool.articleHtmlQueue.put(htmlByRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
