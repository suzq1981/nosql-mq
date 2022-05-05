package com.badou.nosqlmq.redis.base.pubsub;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import com.badou.nosqlmq.redis.base.pool.JedisPoolInstance;

public class RedisSubcriber extends JedisPubSub {

	@Override
	public void onMessage(String channel, String message) {
		System.out.println(Thread.currentThread().getName() + ": channel=" + channel + ", message=" + message);
	}

	public static void main(String[] args) {

		new Thread(() -> {
			Jedis jedis = JedisPoolInstance.getJedis();
			jedis.subscribe(new RedisSubcriber(), "news-badou");
		}, "US").start();

		new Thread(() -> {
			Jedis jedis = JedisPoolInstance.getJedis();
			jedis.subscribe(new RedisSubcriber(), "news-badou");
		}, "CN").start();
	}

}
