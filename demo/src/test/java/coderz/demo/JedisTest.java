package coderz.demo;

import redis.clients.jedis.Jedis;

public class JedisTest {
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Jedis jedis = new Jedis("localhost");
		long s = System.currentTimeMillis();
		jedis.lpush("test", "123456");
		System.out.println(jedis.lpop("test"));
		long e = System.currentTimeMillis();
		System.out.println(e-s);
	}
}	
