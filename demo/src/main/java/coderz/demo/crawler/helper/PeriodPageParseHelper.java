package coderz.demo.crawler.helper;

import java.text.SimpleDateFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import coderz.demo.crawler.entity.Period;

public class PeriodPageParseHelper {
	private static final String periodPageDateFmt = "yyyy-MM-dd";
	
	public static Period[] parsePeriodPage(String html){
		try {
			Document doc = Jsoup.parse(html);
			Elements periodEles = doc.select("period");
			SimpleDateFormat fmt = new SimpleDateFormat(periodPageDateFmt);
			Period[] periods = new Period[periodEles.size()];
			for(int i=0;i<periodEles.size();i++){
				Element ele = periodEles.get(i);
				String dateStr = ele.select("period_date").text();
				String subfix = ele.select("front_page").text();
				Period p = new Period();
				p.setDate(fmt.parse(dateStr));
				p.setFrontPage(subfix);
				periods[i] = p;
			}
			return periods;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Period[0];
	}
}
