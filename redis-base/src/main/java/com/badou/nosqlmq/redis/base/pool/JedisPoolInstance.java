package com.badou.nosqlmq.redis.base.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class JedisPoolInstance {

	private static class JedisPoolHolder {
		
		private static volatile JedisPool jedisPool;
		private static JedisPoolConfig config = new JedisPoolConfig();

		static {
			config.setMaxTotal(100);// 最大连接数
			config.setMaxIdle(2);// 最大空闲连接数
			config.setMaxWaitMillis(60 * 1000);// 获取连接时的最大等待毫秒数
			config.setTestOnBorrow(true);// 在获取连接的时候检查连接有效性
			jedisPool = new JedisPool(config, "192.168.2.200", 6379);
		}
	}

	private JedisPoolInstance() {
	}

	public static JedisPool getInstance() {
		return JedisPoolHolder.jedisPool;
	}

	public static Jedis getJedis() {
		return JedisPoolHolder.jedisPool.getResource();
	}

	public static void main(String[] args) {
		ExecutorService es = Executors.newFixedThreadPool(100);
		for (int i = 0; i < 10; i++) {
			es.execute(new Runnable() {
				@Override
				public void run() {
					System.out.println(JedisPoolInstance.getInstance());
				}
			});
		}

		es.shutdown();
	}

}
