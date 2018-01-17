package com.yida.spider.huxiu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.yida.spider.huxiu.Dao.ArticleDao;
import com.yida.spider.huxiu.pojo.Article;
import com.yida.spider.huxiu.pojo.HuxiuPagingResponse;
import com.yida.spider.huxiu.thread.ParseHtmlRunnable;
import com.yida.spider.huxiu.thread.ProcessSinglePageRunnable;
import com.yida.spider.huxiu.thread.SaveArticleRunnable;



public class HuxiuSpiderThreadPool {
	// 保存数据
		public static ArticleDao articleDao = new ArticleDao();
		
	//创建固定线程池
	private static ExecutorService threadPool = Executors.newFixedThreadPool(30);
	
	// dataline用来做分页的请求
	private static String dateLine = null;
		
	//队列---从首页和分页解析出来的文章url，存放在这个队列中
	public static ArrayBlockingQueue<String> urlQueue = new ArrayBlockingQueue<String>(1000);
	
	//队列---每个文章解析出来的html文档，放在这个队列中
	public static ArrayBlockingQueue<String> articleHtmlQueue = new ArrayBlockingQueue<>(1000);
	
	//队列---每个文章的内容，也就是article对象，存放这个队列中
	public static ArrayBlockingQueue<Article> articleContentQueue = new ArrayBlockingQueue<Article>(1000);
	
	
	public static void main(String[] args) {
		
		
		//提交线程 用来针对每个文章的url ----进行网络请求
		for(int i = 1;i<=10;i++){
			threadPool.execute(new ProcessSinglePageRunnable());
		}
		
		//解析页面
		for(int i = 1;i<=10;i++){
			threadPool.execute(new ParseHtmlRunnable());
		}
		
		//保存数据到数据库
			for(int i =1;i<=5;i++){
				threadPool.execute(new SaveArticleRunnable());
			}
		//解析首页的url获取aid
		processIndexHtml();
		//解析分页的url
		processPageHtml();
		
	}

	/**
	 * 解析分页的url
	 */
	private static void processPageHtml() {
		for(int page = 2;page<=1615;page++){
			//编写分页
			String pageUrl = "https://www.huxiu.com/v2_action/article_list";
			
			System.out.println(page);
			//发送一个post请求
			HttpPost httpPost = new HttpPost(pageUrl);
			//设置参数
			ArrayList<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("huxiu_hash_code","2d9f6e6435e2b71a449ac39a46afe24d"));
			parameters.add(new BasicNameValuePair("page", page+""));
			parameters.add(new BasicNameValuePair("last_dateline", dateLine));
			
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(parameters));
				String jsonText = getHtmlByRequest(httpPost);
				//将json转换为对象
				Gson gson = new Gson();
				HuxiuPagingResponse huxiuPagingResponse = gson.fromJson(jsonText, HuxiuPagingResponse.class);
				// 每一次请求，都需要解析出新的dataLine
				dateLine = huxiuPagingResponse.getLast_dateline();
				// 获取数据
				String htmlData = huxiuPagingResponse.getData();
				
				Document doc = Jsoup.parse(htmlData);
				// 解析出div的某个属性data-id
				Elements aidElements = doc.select("div[data-aid]");
				// 依次得到每个新闻的aid
				for (Element element : aidElements) {
					String aid = element.attr("data-aid");
					urlQueue.put(aid);
				}
				
			} catch (Exception e) {
				System.out.println("出错的页码是："+page);
				e.printStackTrace();
			}
			
		}
		
		
	}

	/**
	 * 执行post或者get请求的方法
	 * @author zhoujie
	 * @param httpPost
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String getHtmlByRequest(HttpRequestBase request) throws ClientProtocolException, IOException {
		//创建httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		//设置user-agent
		request.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
		CloseableHttpResponse closeableHttpResponse = httpClient.execute(request);
		String html = null;
		if(closeableHttpResponse.getStatusLine().getStatusCode() == 200){
			//得到服务端返回的二进制数据
			HttpEntity entity = closeableHttpResponse.getEntity();
			//将二进制数据转化成字符串
			html = EntityUtils.toString(entity,"utf-8");
			return html;
		}
		
		return html;
		
	}

	/**
	 * 解析首页的url获取aid
	 */
	private static void processIndexHtml() {
		//准备一个url
		String url = "https://www.huxiu.com/";
		//创建httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		//发送一个get请求
		HttpGet httpGet = new HttpGet(url);
		//设置user-agent
		//User-Agent:
		httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
		String html = null;
		try {
			//执行get请求
			CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpGet);
			//得到服务端返回的二进制数据
			HttpEntity response = closeableHttpResponse.getEntity();
			//将二进制数据转化成字符串
			html = EntityUtils.toString(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//使jsoup解析html文档
		Document document = Jsoup.parse(html);
		//获取data_line
		dateLine = document.select("[data-last_dateline]").get(0).attr("data-last_dateline");
		System.out.println("第一次的分页参数dateline为："+dateLine);
		Elements elements = document.select("div[data-aid]");
		for (Element e: elements){
			try {
				urlQueue.put(e.attr("data-aid"));
			} catch (InterruptedException e1) {
				System.out.println("添加 aid 到urlQueue异常" + e);
			}
		}
		
	
	}
}
