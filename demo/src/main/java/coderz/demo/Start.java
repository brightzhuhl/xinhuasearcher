package coderz.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import coderz.demo.crawler.XinHuaPaperCrawler;
import coderz.demo.search.IndexTask;

public class Start {
	public static void main(String[] args) {
		new Thread(()->{
			XinHuaPaperCrawler crawler = new XinHuaPaperCrawler();
			String start = "2016/09",end = "2017/07";
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM");
			try {
				crawler.startCrawl(fmt.parse(start), fmt.parse(end));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		},"crawlerMain").start();;
		new Thread(()->{
			IndexTask.beginTask();
		},"indexTaskDispather").start();
	}
}
