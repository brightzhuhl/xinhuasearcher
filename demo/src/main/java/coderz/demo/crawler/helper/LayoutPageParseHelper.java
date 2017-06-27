package coderz.demo.crawler.helper;

import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import coderz.demo.Constant;
import coderz.demo.crawler.entity.Article;
import coderz.demo.crawler.entity.ArticleSummary;
import coderz.demo.crawler.entity.PaperLayout;
import coderz.demo.util.RequestTask;
import coderz.demo.util.httpclient.TransitionHandler;
import coderz.demo.util.sql.Dao;

public class LayoutPageParseHelper extends TransitionHandler {
	private static final String saveLayout = "insert into layout (url,layoutName,dateStr,layoutImg) values(?,?,?,?)";
	public LayoutPageParseHelper(CloseableHttpClient client) {
		super(client);
	}

	@Override
	protected void handle(String res) {
		Document doc = Jsoup.parse(res);
		PaperLayout layout = new PaperLayout();
		
		@SuppressWarnings("unchecked")
		List<PaperLayout> layouts = (List<PaperLayout>) localContext.getAttribute("layouts");
		String url = (String) localContext.getAttribute("url");
		String date = (String) localContext.getAttribute("date");
		layout.setDate(date);
		layout.setUrl(url);
		String name = doc.select("#layout").text();
		layout.setLayoutName(name);
		
		
		String layoutPreView = doc.select(".newspaper-pic img.preview").attr("src");
		layout.setLayoutImg("http://xh.xhby.net/mp3/pc/"+layoutPreView.replace("../../../", ""));
		
		Elements articleEles = doc.select("#articlelist a");
		Article[] articles = new Article[articleEles.size()];
		for(int i=0; i<articleEles.size(); i++){
			Article article = new Article();
			article.setDate(date);
			article.setLayoutUrl(url);
			ArticleSummary summary = new ArticleSummary();
			summary.setTitle(articleEles.get(i).text());
			String articleUrl = "http://xh.xhby.net/mp3/pc/"+articleEles.get(i).attr("href").replace("../../../", "");
			summary.setUrl(articleUrl);
			article.setSummary(summary);
			articles[i] = article;
		}
		layout.setArticles(articles);
		layouts.add(layout);
		
		Elements next = doc.select(".news-title .pull-right a");
		if(next.size()>0 && "下一版".equals(next.last().text())){
			String nextUrl = "http://xh.xhby.net/mp3/pc/layout/"+date+"/"+next.last().attr("href");
			//logger.info(name+","+nextUrl);
			localContext.setAttribute("url", nextUrl);
			HttpGet get = new HttpGet(nextUrl);
			LayoutPageParseHelper handler = new LayoutPageParseHelper(client);
			handler.setLocalContext(localContext);
			handler.setHandleExecutorService(handleExecutorService);
			handler.setRequestExecutorService(requestExecutorService);
			RequestTask task = new RequestTask(client, get, handler);
			task.setContext(localContext);
			requestExecutorService.execute(task);
		}
		else{
			crawlArticlePage(layouts);
		}
	}
	private void crawlArticlePage(List<PaperLayout> layouts){
		int articleNum = 0;
		for(PaperLayout layout:layouts){
			String url = layout.getUrl(),name = layout.getLayoutName(),dateStr = layout.getDate(),img = layout.getLayoutImg();
			Constant.sqlThreadPool.execute(()->{
				Dao.execute(saveLayout, url,name,dateStr,img);
			});
			Article[] articles = layout.getArticles();
			for(Article article:articles){
				String articleUrl = article.getSummary().getUrl();
				HttpGet get = new HttpGet(articleUrl);
				HttpClientContext context = new HttpClientContext();
				ArticlePageParseHelper handler = new ArticlePageParseHelper(context, handleExecutorService);
				RequestTask task = new RequestTask(client, get, handler);
				context.setAttribute("article", article);
				task.setContext(context);
				requestExecutorService.execute(task);
				articleNum++;
			}
		}
		synchronized (Constant.layoutLock) {
			Constant.layoutLock.notifyAll();
			System.out.println("finished:"+layouts.get(0).getDate()+","+layouts.size()+","+articleNum);
		}
	}
}
