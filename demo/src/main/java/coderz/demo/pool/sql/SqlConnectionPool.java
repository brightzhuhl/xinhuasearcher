package coderz.demo.pool.sql;

import java.sql.Connection;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import coderz.demo.Constant;
import coderz.demo.util.PropertiesUtil;

public class SqlConnectionPool extends GenericObjectPool<Connection> {
	
	private static int maxTotalConnection = PropertiesUtil.getIntValue(Constant.SQL_CON_NUM);;
	
	private static Object objLock = new Object();
	
	private static SqlConnectionPool pool;
	
	
	private SqlConnectionPool(PooledObjectFactory<Connection> factory,GenericObjectPoolConfig config) {
		super(factory,config);
	}
	
	
	
	public static SqlConnectionPool getInstance(){
		synchronized (objLock) {
			if(pool == null){
				GenericObjectPoolConfig config = new GenericObjectPoolConfig();
				config.setMaxTotal(maxTotalConnection);
				pool = new SqlConnectionPool(new PooledSqlConnectionFactory(),config);
			}
		}
		return pool;
	}
}
