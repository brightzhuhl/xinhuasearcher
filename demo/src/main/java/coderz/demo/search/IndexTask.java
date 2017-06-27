package coderz.demo.search;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import com.alibaba.fastjson.JSONObject;

import coderz.demo.Constant;
import coderz.demo.crawler.entity.Article;
import coderz.demo.util.PropertiesUtil;
import coderz.demo.util.redis.CrawlerResponseRedisBuffer;
import coderz.demo.util.sql.Dao;

public class IndexTask implements Runnable {
	
	static Log logger = LogFactory.getLog(IndexTask.class);
	
	private Article article;
	
	public IndexTask(Article article) {
		this.article = article;
	}


	public static void beginTask(){
		CrawlerResponseRedisBuffer buffer = new CrawlerResponseRedisBuffer();
		while(true){
			String str = buffer.takeOne();
			Article article = JSONObject.parseObject(str, Article.class);
			if(StringUtils.isEmpty(article.getSummary().getTitle())){
				continue;
			}
			if(StringUtils.isEmpty(article.getContent())){
				continue;
			}
			IndexTask task = new IndexTask(article);
			Constant.luceneThreadPool.execute(task);
			String url = article.getSummary().getUrl(),title = article.getSummary().getTitle(),content = article.getContent(),
					dateStr= article.getDate(),layoutUrl = article.getLayoutUrl();
			Constant.sqlThreadPool.execute(()->{
				Dao.execute("insert into article (url,title,content,dateStr,layoutUrl) values(?,?,?,?,?)", url,title,content,dateStr,layoutUrl);
			});
		}
	}
	
	
	@Override
	public void run() {
		try {
			indexArticle();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	private void indexArticle() throws IOException{
		long start = System.currentTimeMillis();
		IndexWriter indexWriter = DefaultIndexWriter.getInstance();
		
		Document doc = new Document();
		IndexableField content = new TextField(Constant.ARTICLE_CONTENT, article.getContent(),Store.YES);
		IndexableField date = new TextField(Constant.ARTICLE_DATE, article.getDate(),Store.YES);
		IndexableField title = new TextField(Constant.ARTICLE_TITLE, article.getSummary().getTitle(),Store.YES);
		IndexableField layoutUrl = new TextField(Constant.ARTICLE_LAYOUT_URL, article.getLayoutUrl(),Store.YES);
		IndexableField author = new TextField(Constant.ARTICLE_AUTHOR, article.getAuthor(),Store.YES);
		IndexableField url = new TextField(Constant.ARTICLE_URL, article.getSummary().getUrl(),Store.YES);
		
		
		doc.add(content);
		doc.add(date);
		doc.add(title);
		doc.add(author);
		doc.add(layoutUrl);
		doc.add(url);
		
		indexWriter.addDocument(doc);
		indexWriter.commit();
		long end = System.currentTimeMillis();
		logger.info(article.getSummary().getTitle()+",完成索引,用时:"+(end-start)+" ms");
		
	}
	
	public static class DefaultIndexWriter extends IndexWriter{
		
		private static DefaultIndexWriter indexWriter;
		
		private DefaultIndexWriter(Directory dir,IndexWriterConfig config) throws IOException{
			super(dir, config);
		}
		
		public static synchronized DefaultIndexWriter getInstance(){
			
			if(indexWriter != null){
				return indexWriter;
			}
			String indexPath = PropertiesUtil.getStringValue(Constant.LUCENE_INDEX_PATH);
			
			Analyzer analyzer = new SmartChineseAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			
			try {
				Directory dir = SimpleFSDirectory.open(Paths.get(indexPath));
				DefaultIndexWriter indexWriter = new DefaultIndexWriter(dir, config);
				DefaultIndexWriter.indexWriter = indexWriter;
				return DefaultIndexWriter.indexWriter;
			} catch (IOException e) {
				logger.error(e.getMessage());
				throw new RuntimeException("lucene get indexWriter failed,"+e.getMessage());
			}
		}
		
		public static void closeIns(){
			if(indexWriter != null){
				try {
					indexWriter.close();
					indexWriter = null;
				} catch (IOException e) {
					logger.error("close indexWriter error,"+e.getMessage());
				}
			}
		}
	}
	
}
