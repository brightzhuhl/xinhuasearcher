package coderz.demo.search.ik;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.naming.OperationNotSupportedException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import coderz.demo.Constant;
import coderz.demo.crawler.entity.Article;
import coderz.demo.crawler.entity.ArticleSummary;
import coderz.demo.search.IndexResolver;
import coderz.demo.util.PropertiesUtil;
import coderz.demo.util.sql.Dao;

public class IKIndexResolver extends IndexResolver{

	@Override
	protected void setCustomField(Document doc, Article article) {
		BytesRef bytesRef = new BytesRef(article.getId());
		SortedDocValuesField idField = new SortedDocValuesField("id", bytesRef);
		doc.add(idField);
	}

	@Override
	protected IndexWriter customIndexWriter() {
		try {
			String path = PropertiesUtil.getStringValue(Constant.LUCENE_INDEX_PATH);
			Directory directory = FSDirectory.open(Paths.get(path));
			Analyzer analyzer = new Lucene6IKAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			return new IndexWriter(directory, config);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		final IndexResolver resolver = new IKIndexResolver();
		
		resolver.init();
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		
		final String sql = "select id,url,title,content,layoutUrl,dateStr from article limit ?,1000";
		
		for(int i=0; i<10; i++){
			int oristart = i*2400;
			executorService.execute(()->{
				int mystart = oristart;
				List<String[]> articleLi = Dao.query(6,sql,mystart);
				while(articleLi.size() > 0){
					for(String[] articleIte: articleLi){
						Article article = new Article();
						
						ArticleSummary summary = new ArticleSummary();
						
						article.setId(articleIte[0]);
						
						summary.setUrl(articleIte[1]);
						summary.setTitle(articleIte[2]);
						article.setSummary(summary);
						
						article.setContent(articleIte[3]);
						article.setLayoutUrl(articleIte[4]);
						article.setDate(articleIte[5]);
						try {
							resolver.addArticle(article);
							logger.info("索引成功:"+article.getSummary().getTitle());
						} catch (OperationNotSupportedException e) {
							logger.error(e.getMessage());
						}
					}
					resolver.flushSegment();
					logger.info("完成:"+mystart);
					if(mystart >= oristart+2400){
						break;
					}
					mystart += 1000;
					articleLi = Dao.query(6,sql,mystart);
				}
				
			});
		}
		
	}
}
