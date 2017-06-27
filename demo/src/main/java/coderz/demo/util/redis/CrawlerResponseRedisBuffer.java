package coderz.demo.util.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import coderz.demo.pool.jedis.JedisPool;
import redis.clients.jedis.Jedis;

public class CrawlerResponseRedisBuffer {
	
	public static Log logger = LogFactory.getLog(CrawlerResponseRedisBuffer.class);
	
	private static String responseQueueKey = "coderz_crawler_xhrb_demo_res"; 
	
	public static Object indexSynchronizer = new Object();
	
	public void write(String str){
		JedisPool jedisPool = JedisPool.getInstance();
		Jedis jedis = null;
		try {
			jedis = jedisPool.borrowObject();
			jedis.rpush(responseQueueKey, str);
			synchronized (indexSynchronizer) {
				indexSynchronizer.notify();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			jedisPool.returnObject(jedis);
		}
	}

	
	
	public String takeOne(){
		JedisPool jedisPool = JedisPool.getInstance();
		Jedis jedis = null;
		try {
			jedis = jedisPool.borrowObject();
			String str = jedis.lpop(responseQueueKey);
			while(str == null){
				synchronized (indexSynchronizer) {
					indexSynchronizer.wait();
				}
				str = jedis.lpop(responseQueueKey);
			}
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			jedisPool.returnObject(jedis);
		}
	}
}
