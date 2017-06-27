package coderz.demo.util;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil{

	private static Properties properties = null;
	
	static{
		properties = new Properties();
		try {
			properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static String getStringValue(String key){
		return properties.getProperty(key);
	}
	
	public static int getIntValue(String key){
		return Integer.parseInt(properties.getProperty(key));
	}
	
	public static long getLongValue(String key){
		return Long.parseLong(properties.getProperty(key));
	}
	
	public static double getDoubleValue(String key){
		return Double.parseDouble(properties.getProperty(key));
	}
	
	public static float getFloatValue(String key){
		return Float.parseFloat(properties.getProperty(key));
	}
	
	public static boolean getBooleanValue(String key){
		return Boolean.parseBoolean(properties.getProperty(key));
	}
}
