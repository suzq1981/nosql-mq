package com.badou.nosqlmq.redis.base.pubsub;

import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;

import com.badou.nosqlmq.redis.base.pool.JedisPoolInstance;

public class RedisPublisher {

	public static void main(String[] args) throws Exception {

		Jedis jedis = JedisPoolInstance.getJedis();

		for (int i = 1; i <= 10; i++) {
			jedis.publish("news-badou", "突破重大发现" + i);
			TimeUnit.MILLISECONDS.sleep(1000);
		}
		
		jedis.close();
	}
}
