package coderz.demo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cn.smart.HMMChineseTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StandardDirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;

import coderz.demo.search.Searcher;
import coderz.demo.search.standard.AbstractSearcher;

public class LuceneTest {
	final static String dataFilePath = "D:\\lucenedata\\source\\test.txt";
	final static String indexDirPath = "D:\\lucenedata\\index";
	public static void main(String[] args){
		Constant.init();
		Searcher searcher = AbstractSearcher.createDefault();
		System.out.println(searcher.searchWithMustContainPolicy("中国农业"));
	}
	public static void analyse(String str){
		 Tokenizer tokenizer = new HMMChineseTokenizer();
		 tokenizer.setReader(new StringReader(str));
		 try {
			tokenizer.reset();
			do {
				tokenizer.reflectWith(new AttributeReflector() {
					@Override
					public void reflect(Class<? extends Attribute> attClass, String key, Object value) {
						if("term".equals(key))
							System.out.println(key+":"+value);
					}
				});
			} while (tokenizer.incrementToken());
			tokenizer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void index(){
		try {
			File file = new File(dataFilePath);
			long start = System.currentTimeMillis();
			Document doc = new Document();
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter indexWriter = new IndexWriter(NIOFSDirectory.open(Paths.get(indexDirPath)),config);
			doc.add(new TextField("path", file.toString(),Store.YES));
			doc.add(new TextField("content", new FileReader(file)));
			indexWriter.addDocument(doc);
			long id = indexWriter.commit();
			indexWriter.close();
			long end = System.currentTimeMillis();
			System.out.println("finished with "+(end - start)+" ms,id:"+id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public static void search(){
		IndexReader reader;
		try {
			reader = StandardDirectoryReader.open(NIOFSDirectory.open(Paths.get(indexDirPath)));
			IndexSearcher searcher = new IndexSearcher(reader);
			QueryParser parser = new QueryParser("content", new StandardAnalyzer());
			Query query = parser.parse("lucene");
			searcher.search(query, 100);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void standardAnalyzerTest(){
		StandardAnalyzer analyzer = new StandardAnalyzer();
		TokenStream tokenStream = analyzer.tokenStream("test", "“Students”，“allowed”，“go”，“their”，“friends”，“allowed”，“drink”，“beer”，“My”，“friend”，“Jerry”，“went”，“school”，“see”，“his”，“students”，“found”，“them”，“drunk”，“allowed”");
		
		try {
			/*do{
				System.out.println(tokenStream.reflectAsString(true));
				System.out.println(tokenStream.reflectAsString(false));
				tokenStream.reset();
			}while(tokenStream.incrementToken());*/
			tokenStream.reset();
			while(tokenStream.incrementToken()){
				System.out.println(tokenStream.reflectAsString(false));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		analyzer.close();
	}
}
