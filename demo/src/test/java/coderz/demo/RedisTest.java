package coderz.demo;

import coderz.demo.util.redis.CrawlerResponseRedisBuffer;

public class RedisTest {
	public static void main(String[] args) {
		CrawlerResponseRedisBuffer buffer = new CrawlerResponseRedisBuffer();
		while(true){
			String result = buffer.takeOne();
			System.out.println(result);
		}
	}
}
