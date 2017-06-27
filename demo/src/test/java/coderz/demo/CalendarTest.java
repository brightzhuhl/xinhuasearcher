package coderz.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarTest {
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMM");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Calendar end = Calendar.getInstance();
		end.setTime(fmt.parse("20170701"));
		while (cal.before(end)) {
			System.out.println(fmt.format(cal.getTime()));
			cal.add(Calendar.HOUR, 24);
		}
	}
}
