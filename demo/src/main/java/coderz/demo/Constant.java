package coderz.demo;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.StandardDirectoryReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import coderz.demo.search.SearchUtil.CompareableTerm;
import coderz.demo.util.PropertiesUtil;
import coderz.demo.util.ScheduleExcutor;

public class Constant {
	
	private static Log logger = LogFactory.getLog(Constant.class);

	public static Object layoutLock= new Object();
	
	public static ExecutorService requestThreadPool,redisThreadPool,sqlThreadPool,
					processThreadPool,luceneThreadPool,luceneSearchPool;
	
	public static CloseableHttpClient client;
	
	
	/**
	 * redis config constant
	 */
	public static final String REDIS_HOST = "redis.host",REDIS_CON_NUM = "redis.maxTotalConnection";
	
	
	/**
	 * sql config constant
	 */
	public static final String SQL_URL = "sql.url", SQL_DRIVER = "sql.driver",SQL_USERNAME = "sql.userName",
								SQL_PASSWORD = "sql.password",SQL_CON_NUM = "sql.conNum";
	
	
	/**
	 * scheduleExecutor constant
	 */
	public static final String SCHEDULE_EXECUTOR_TASKSIZE = "scheduleExecutor.taskSize",
								SCHEDULE_EXECUTOR_POOLSIZE = "scheduleExecutor.poolSize",
								SCHEDULE_EXECUTOR_INIT_DELAY = "scheduleExecutor.initDelay",
								SCHEDULE_EXECUTOR_PERIOD = "scheduleExecutor.period";
	
	
	/**
	 * crawler constant
	 */
	private static final String REQUEST_THREAD_NUM = "crawler.requestThreadNum",REDIS_THREAD_NUM = "crawler.redisThreadNum",
								SQL_THREAD_NUM = "crawler.sqlThreadNum",PROCESS_THREAD_NUM = "crawler.processThreadNum";
	
	
	private static final String HTTPCLIENT_CON_NUM = "crawler.httpclientConNum",
								HTTPCLIENT_CON_NUM_PER_ROUTE = "crawler.httpclientConNumPerRoute";
	
	/**
	 * lucene constant
	 */
	public static final String LUCENE_INDEX_PATH = "lucene.indexPath",LUCENE_INDEX_THREAD_NUM = "lucene.indexThreadNum",
								LUCENE_SEARCH_T_NUM = "lucene.searchThreadNum";
					
	public static IndexWriter indexWriter;
	
	public static IndexReader indexReader;
	
	public static IndexSearcher indexSearcher;
	
	public static Map<String, Integer> termIndex;
	
	public static List<CompareableTerm> terms ;
	
	private static Object termLock = new Object();
	
	public static String ARTICLE_CONTENT = "content" , ARTICLE_AUTHOR = "author" ,ARTICLE_URL = "URL",
								ARTICLE_TITLE = "title",ARTICLE_DATE = "date",ARTICLE_LAYOUT_URL ="layoutUrl";
	
	public static void init(){
		
		//初始化线程池
		
		requestThreadPool = new ScheduleExcutor(PropertiesUtil.getIntValue(REQUEST_THREAD_NUM));
		
		redisThreadPool = Executors.newFixedThreadPool(PropertiesUtil.getIntValue(REDIS_THREAD_NUM));
		
		sqlThreadPool = Executors.newFixedThreadPool(PropertiesUtil.getIntValue(SQL_THREAD_NUM));
		
		processThreadPool = Executors.newFixedThreadPool(PropertiesUtil.getIntValue(PROCESS_THREAD_NUM));
			
		luceneThreadPool = Executors.newFixedThreadPool(PropertiesUtil.getIntValue(LUCENE_INDEX_THREAD_NUM));
		
		luceneSearchPool = Executors.newFixedThreadPool(PropertiesUtil.getIntValue(LUCENE_SEARCH_T_NUM));
		
		client = HttpClients.custom().setMaxConnTotal(PropertiesUtil.getIntValue(HTTPCLIENT_CON_NUM)).
				setMaxConnPerRoute(PropertiesUtil.getIntValue(HTTPCLIENT_CON_NUM_PER_ROUTE)).build();
		
		//初始化indexReader,indexWriter,indexSearcher,termIndex
		
		long start = System.currentTimeMillis(),end;
		String indexPath = PropertiesUtil.getStringValue(Constant.LUCENE_INDEX_PATH);
		
		Directory dir;
		try {
			dir = FSDirectory.open(Paths.get(indexPath));
			indexReader = StandardDirectoryReader.open(dir);
			indexSearcher = new IndexSearcher(indexReader, Constant.luceneSearchPool);
			end =System.currentTimeMillis();
			logger.info("indexReader,indexSearcher 初始化用时："+(end-start));
			Fields fields = MultiFields.getFields(indexReader);
			termIndex = new HashMap<>();
			fields.forEach((f)->{
				if(Constant.ARTICLE_CONTENT.equals(f)){
					try {
						Terms terms = fields.terms(f);
						TermsEnum termsEnum = terms.iterator();
						BytesRef bytesRef = null;
						while((bytesRef = termsEnum.next()) != null){
							String term = new String(bytesRef.bytes,bytesRef.offset,bytesRef.length);
							termIndex.put(term, termsEnum.docFreq());
						}
					} catch (IOException e1) {
						logger.error(e1.getMessage());
					}
					
				}
			});
		} catch (IOException e) {
			logger.error("获取indexReader失败,"+e.getMessage());
			throw new RuntimeException("获取indexReader失败,"+e.getMessage());
		}
		//初始化terms列表
		if(terms == null){
			start = System.currentTimeMillis();
			synchronized (termLock) {
				if(terms == null){
					terms = new ArrayList<>();
					Iterator<String> ite = termIndex.keySet().iterator();
					while(ite.hasNext()){
						String key = ite.next();
						if(key.length()>=2&&key.matches("[^\\da-z]*"))
							terms.add(new CompareableTerm(termIndex.get(key), key));
					}
					Collections.sort(terms);
				}
			}
			end = System.currentTimeMillis();
			logger.info("terms列表初始化完成，用时："+(end-start)+" ms");
		}
	}
}
