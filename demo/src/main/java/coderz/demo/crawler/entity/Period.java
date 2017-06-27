package coderz.demo.crawler.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Period {
	
	private static final String layoutUrl = "http://xh.xhby.net/mp3/pc/layout/yyyyMM/dd/l1.html";
	
	private static final String layoutUrlDateFormat = "yyyyMM/dd";
	
	
	private Date date;
	
	private String frontPage;

	public static String getLayoutUrlByPeriod(Period p){
		SimpleDateFormat sdf = new SimpleDateFormat(layoutUrlDateFormat);
		String dateFmtStr = sdf.format(p.getDate());
		return layoutUrl.replace(layoutUrlDateFormat, dateFmtStr);
	}
	
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFrontPage() {
		return frontPage;
	}

	public void setFrontPage(String frontPage) {
		this.frontPage = frontPage;
	}
	
	
}
