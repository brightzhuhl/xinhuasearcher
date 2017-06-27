package coderz.demo.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import coderz.demo.Constant;

@WebListener
public class ConstantInitListener implements ServletContextListener{
	
	protected static Log logger = LogFactory.getLog(ConstantInitListener.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Constant.init();
		try {
			long start = System.currentTimeMillis();
			Class.forName("org.apache.lucene.queryparser.classic.QueryParser");
			Class.forName("org.apache.lucene.analysis.cn.smart.HMMChineseTokenizer");
			Class.forName("coderz.demo.search.ik.Lucene6IKAnalyzer");
			long end = System.currentTimeMillis();
			logger.info("lucene相关类加载完成，用时："+(end-start)+" ms");
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		}
	}

}
