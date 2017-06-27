package coderz.demo.pool.jedis;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import coderz.demo.Constant;
import coderz.demo.util.PropertiesUtil;
import redis.clients.jedis.Jedis;

public class JedisPool extends GenericObjectPool<Jedis> {
	
	private static int maxTotalConnection = PropertiesUtil.getIntValue(Constant.REDIS_CON_NUM);
	
	private static JedisPool jedisPool;
	
	private JedisPool(PooledObjectFactory<Jedis> factory, GenericObjectPoolConfig config) {
		super(factory, config);
	}
	
	
	
	public static synchronized JedisPool getInstance(){
		if(jedisPool == null){
			GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMaxTotal(maxTotalConnection);
			jedisPool = new JedisPool(new PooledJedisObjectFactory(), config);
		}
		return jedisPool;
	}
}
