package coderz.demo.pool.jedis;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import coderz.demo.Constant;
import coderz.demo.util.PropertiesUtil;
import redis.clients.jedis.Jedis;

public class PooledJedisObjectFactory implements PooledObjectFactory<Jedis> {
	
	private static final String redisServer = PropertiesUtil.getStringValue(Constant.REDIS_HOST);
	
	@Override
	public PooledObject<Jedis> makeObject() throws Exception {
		Jedis jedis = new Jedis(redisServer);
		PooledObject<Jedis> pooledObject = new DefaultPooledObject<Jedis>(jedis);
		return pooledObject;
	}

	@Override
	public void destroyObject(PooledObject<Jedis> jedis) throws Exception {
		jedis.getObject().close();
	}

	@Override
	public boolean validateObject(PooledObject<Jedis> jedis) {
		if(!jedis.getObject().isConnected())
			return false;
		return false;
	}

	@Override
	public void activateObject(PooledObject<Jedis> p) throws Exception {
	}

	@Override
	public void passivateObject(PooledObject<Jedis> p) throws Exception {
	}

}
