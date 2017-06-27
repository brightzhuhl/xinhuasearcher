package coderz.demo.crawler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import coderz.demo.Constant;
import coderz.demo.crawler.entity.PaperLayout;
import coderz.demo.crawler.entity.Period;
import coderz.demo.crawler.helper.LayoutPageParseHelper;
import coderz.demo.crawler.helper.PeriodPageParseHelper;
import coderz.demo.util.RequestTask;

public class XinHuaPaperCrawler {
	
	//private static final String paperLayoutUrl = "http://xh.xhby.net/mp3/pc/layout/yyyyMM/dd/l1.html";
	
	private static final String periodUrl = "http://xh.xhby.net/mp3/pc/layout/yyyyMM/period.xml";
		
	private static final String paperLayoutUrlDateFormat = "yyyyMM/dd";
	
	private static final String periodUrlDateFormat = "yyyyMM";
	
	private ExecutorService service,processService;
	
	public void startCrawl(Date start,Date end){
		
		service = Constant.requestThreadPool;
		
		processService = Constant.processThreadPool;
		
		CloseableHttpClient client = Constant.client;
		
		SimpleDateFormat fmt = new SimpleDateFormat(periodUrlDateFormat);
		SimpleDateFormat fmt2 = new SimpleDateFormat(paperLayoutUrlDateFormat);
		
		Calendar sc = Calendar.getInstance(),ec = Calendar.getInstance();
		ec.setTime(end);
		sc.setTime(start);
		while(sc.before(ec)){
			String dateStr = fmt.format(sc.getTime());
			HttpGet get = new HttpGet(periodUrl.replace(periodUrlDateFormat, dateStr));
			try {
				HttpResponse res = client.execute(get);
				Period[] periods = PeriodPageParseHelper.parsePeriodPage(EntityUtils.toString(res.getEntity()));
				for(Period p:periods){
					String layoutPageUrl = Period.getLayoutUrlByPeriod(p);
					HttpClientContext context = new HttpClientContext();
					context.setAttribute("url", layoutPageUrl);
					context.setAttribute("date", fmt2.format(p.getDate()));
					context.setAttribute("layouts", new ArrayList<PaperLayout>());
					getPaperLayout(layoutPageUrl, context, client);
					synchronized (Constant.layoutLock) {
						try {
							Constant.layoutLock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			sc.add(Calendar.MONTH, 1);
		}
		
	}
	
	public void getPaperLayout(String url,HttpClientContext context,CloseableHttpClient client){
		HttpGet get = new HttpGet(url);
		LayoutPageParseHelper handler = new LayoutPageParseHelper(client);
		handler.setLocalContext(context);
		handler.setHandleExecutorService(processService);
		handler.setRequestExecutorService(service);
		RequestTask task = new RequestTask(client, get, handler);
		task.setContext(context);
		service.execute(task);
	}
	
}
