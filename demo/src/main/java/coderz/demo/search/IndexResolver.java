package coderz.demo.search;

import java.io.IOException;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

import coderz.demo.Constant;
import coderz.demo.crawler.entity.Article;
import coderz.demo.search.ik.IKIndexResolver;

public abstract class IndexResolver {
	protected final static Log logger = LogFactory.getLog(IKIndexResolver.class);
	
	protected IndexWriter indexWriter;
	
	public void init(){
		indexWriter = customIndexWriter();
	}
	
	
	public final void addArticle(Article article) throws OperationNotSupportedException{
		if(indexWriter == null){
			throw new OperationNotSupportedException("需要先调用init()方法初始化");
		}
		Document doc = new Document();
		
		setCustomField(doc, article);
		
		TextField content = new TextField(Constant.ARTICLE_CONTENT, article.getContent(), Store.YES);
		if(doc.getField(Constant.ARTICLE_CONTENT) == null)
			doc.add(content);
		
		StringField url = new StringField(Constant.ARTICLE_URL, article.getSummary().getUrl(), Store.YES);
		if(doc.getField(Constant.ARTICLE_URL) == null)
			doc.add(url);
		
		StringField date = new StringField(Constant.ARTICLE_DATE, article.getDate(), Store.YES);
		if(doc.getField(Constant.ARTICLE_DATE) == null)
			doc.add(date);
		
		TextField title = new TextField(Constant.ARTICLE_TITLE, article.getSummary().getTitle(),Store.YES);
		if(doc.getField(Constant.ARTICLE_TITLE) == null)
			doc.add(title);
		
		StringField layoutUrl = new StringField(Constant.ARTICLE_LAYOUT_URL, article.getLayoutUrl(),Store.YES);
		if(doc.getField(Constant.ARTICLE_DATE) == null)
			doc.add(layoutUrl);
		
		/*StringField author = new StringField(Constant.ARTICLE_AUTHOR, article.getAuthor(),Store.YES);
		if(doc.getField(Constant.ARTICLE_DATE) == null)
			doc.add(author);*/
		
		
		try {
			indexWriter.addDocument(doc);
			indexWriter.commit();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	public void flushSegment(){
		try {
			indexWriter.flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	protected abstract void setCustomField(Document doc,Article article);
	
	protected  abstract IndexWriter customIndexWriter();
	
}
