package com.badou.nosqlmq.redis.base.simple;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class RedisKey {

	private static final Logger logger = LoggerFactory.getLogger(RedisKey.class);

	public static void main(String[] args) throws Exception {
		Jedis jedis = new Jedis("192.168.2.200", 6379);

		Set<String> keys = jedis.keys("*");
		logger.info("keys=" + Arrays.toString(keys.toArray()));

		Boolean exist = jedis.exists("RedisString1");
		logger.info("key RedisString1 is exist? " + exist);

		jedis.setex("k1", 15, "v1");
		TimeUnit.SECONDS.sleep(2);

		// 查看key的过期时间
		long ttl = jedis.ttl("RedisString1");
		logger.info("key RedisString1 remaining time to live in seconds " + ttl);
		ttl = jedis.ttl("k1");
		logger.info("key k1 remaining time to live in seconds " + ttl);
		
		jedis.del("k1");
		
		//查看指定key的值类型
		String type = jedis.type("hkey1");
		logger.info("key hkey1 type is " + type);

		jedis.close();
	}

}
