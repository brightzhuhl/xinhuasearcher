package coderz.demo.crawler.helper;

import java.util.concurrent.ExecutorService;

import org.apache.http.client.protocol.HttpClientContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSONObject;

import coderz.demo.Constant;
import coderz.demo.crawler.entity.Article;
import coderz.demo.util.httpclient.FinalHanlder;
import coderz.demo.util.redis.CrawlerResponseRedisBuffer;

public class ArticlePageParseHelper extends FinalHanlder {

	public ArticlePageParseHelper(HttpClientContext context, ExecutorService service) {
		super(context, service);
	}

	@Override
	protected void handle(String res) {
		Article article = (Article) context.getAttribute("article");
		Document doc = Jsoup.parse(res);
		String content = doc.getElementById("ozoom").text();
		String author = null;
		try {
			author = doc.select("#ozoom p").last().text();
		} catch (Exception e) {
			e.printStackTrace();
		}
		article.setAuthor(author);
		article.setContent(content);
		String articleStr = JSONObject.toJSONString(article);
		Constant.redisThreadPool.execute(()->{
			CrawlerResponseRedisBuffer buffer = new CrawlerResponseRedisBuffer();
			buffer.write(articleStr);
			logger.info("save to redis:"+article.getDate()+","+article.getSummary().getTitle());
		});
		content = null;
	}

}
