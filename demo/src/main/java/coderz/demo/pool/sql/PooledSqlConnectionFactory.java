package coderz.demo.pool.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import coderz.demo.Constant;
import coderz.demo.util.PropertiesUtil;

public class PooledSqlConnectionFactory implements PooledObjectFactory<Connection> {
	
	
	private static final String connURL = PropertiesUtil.getStringValue(Constant.SQL_URL);
	
	
	private static final String driverName = PropertiesUtil.getStringValue(Constant.SQL_DRIVER);
	
	
	private static String userName = PropertiesUtil.getStringValue(Constant.SQL_USERNAME);
	
	
	private static String password = PropertiesUtil.getStringValue(Constant.SQL_PASSWORD);
	
	
	private static Log logger = LogFactory.getLog(PooledSqlConnectionFactory.class);
	
	
	static{
		try {
			Class.forName(driverName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public PooledObject<Connection> makeObject() throws Exception {
		Connection conn = DriverManager.getConnection(connURL, userName, password);
		PooledObject<Connection> pooledConnection = new DefaultPooledObject<>(conn);
		if(logger.isDebugEnabled()){
			logger.debug("jdbc连接池创建连接对象："+conn);
		}
		return pooledConnection;
	}

	@Override
	public void destroyObject(PooledObject<Connection> p) throws Exception {
		p.getObject().close();
	}

	@Override
	public boolean validateObject(PooledObject<Connection> p) {
		try {
			return !p.getObject().isClosed();
		} catch (SQLException e) {
			logger.error("连接池验证jdbc连接对象出现异常",e);
		}
		return true;
	}

	@Override
	public void activateObject(PooledObject<Connection> p) throws Exception {
	}

	@Override
	public void passivateObject(PooledObject<Connection> p) throws Exception {
	}

}
