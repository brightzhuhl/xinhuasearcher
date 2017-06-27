package coderz.demo.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.cn.smart.HMMChineseTokenizer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import coderz.demo.Constant;
import coderz.demo.search.ik.Lucene6IKAnalyzer;

public class SearchUtil {
	
	private static Log logger = LogFactory.getLog(SearchUtil.class);
	
	public static class CompareableTerm implements Comparable<CompareableTerm>{
		
		private int count;
		
		private String term;
		
		public CompareableTerm(int count,String term){
			this.count = count;
			this.term = term;
		}
		
		@Override
		public int compareTo(CompareableTerm o) {
			if(o.getCount() > getCount()){
				return -1;
			}else if(o.getCount() == getCount()){
				return 0;
			}
			return 1;
		}
		
		
		public int getCount() {
			return count;
		}
		
		
		public void setCount(int count) {
			this.count = count;
		}
		
		
		public String getTerm() {
			return term;
		}
		
		
		public void setTerm(String term) {
			this.term = term;
		}
		
	}
	
	public static Query getBooleanQuery(String keyword){
		HMMChineseTokenizer tokens = new HMMChineseTokenizer();
		tokens.setReader(new StringReader(keyword));
		Builder mutilQueryBuilder = new Builder();
		try {
			tokens.reset();
			while(tokens.incrementToken()){
				tokens.reflectWith((clazz,key,value)->{
					if("term".equals(key)){
						Term term = new Term(Constant.ARTICLE_CONTENT,value.toString());
						TermQuery query = new TermQuery(term);
						mutilQueryBuilder.add(query,Occur.MUST);
					}
				});
			}
			tokens.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return mutilQueryBuilder.build();
	}
	
	public static Query getDefaultQuery(String keyword){
		QueryParser parser = new QueryParser(Constant.ARTICLE_CONTENT,new SmartChineseAnalyzer());
		try {
			return parser.parse(keyword);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new TermQuery(new Term(Constant.ARTICLE_CONTENT,keyword));
	}
	
	public static Query getDefaultQueryWithIk(String keyword){
		QueryParser parser = new QueryParser(Constant.ARTICLE_CONTENT,new Lucene6IKAnalyzer());
		try {
			return parser.parse(keyword);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new TermQuery(new Term(Constant.ARTICLE_CONTENT,keyword));
	}
	public static JSONArray searchKeyWord(String keyword){
		long start = System.currentTimeMillis();
		Query mutilQuery =	getDefaultQueryWithIk(keyword);
		QueryScorer scorer = new QueryScorer(mutilQuery);
		TopDocs docs = null;
		long end = System.currentTimeMillis();
		logger.info("搜索准备工作完成："+(end-start)+" ms");
		try {
			docs = Constant.indexSearcher.search(mutilQuery,20);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		end = System.currentTimeMillis();
		logger.info("搜索完成："+(end-start)+" ms");
		ScoreDoc[] scoreDocs = docs.scoreDocs;
		
		SimpleHTMLFormatter fmtter = new SimpleHTMLFormatter("<font color=\"red\">","</font>");
		Highlighter highlighter = new Highlighter(fmtter, scorer);
		highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer));
		
		JSONArray results = new JSONArray();
		for(ScoreDoc scoreDoc : scoreDocs){
			try {
				JSONObject item = new JSONObject();
				Document doc = Constant.indexSearcher.doc(scoreDoc.doc);
				
				item.put("title", doc.get(Constant.ARTICLE_TITLE));
				String content = doc.get(Constant.ARTICLE_CONTENT);
				item.put("content", highlighter.getBestFragment(new Lucene6IKAnalyzer(),Constant.ARTICLE_CONTENT,content));
				
				item.put("url", doc.get(Constant.ARTICLE_URL));
				item.put("date", doc.get(Constant.ARTICLE_DATE));
				item.put("score", scoreDoc.score);
				results.add(item);
			} catch (IOException | InvalidTokenOffsetsException e) {
				logger.error(e.getMessage());
			}
		}
		end = System.currentTimeMillis();
		logger.info("搜索关键词："+keyword+",用时："+(end-start)+" ms");
		return results;
	}
	
	
	public static JSONObject getKeyword(int num){
		JSONObject result = new JSONObject();
		List<CompareableTerm> terms = Constant.terms;
		for(int i=terms.size()-1; i>= (terms.size()- num); i--){
			result.put(terms.get(i).getTerm(), terms.get(i).getCount());
		}
		return result;
	}
	
	
	public static void main(String[] args) {
		System.out.println(searchKeyWord("农业人口比例"));
	}
}
