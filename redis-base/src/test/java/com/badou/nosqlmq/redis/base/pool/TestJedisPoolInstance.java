package com.badou.nosqlmq.redis.base.pool;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TestJedisPoolInstance {

	private static final Logger logger = LoggerFactory.getLogger(TestJedisPoolInstance.class);

	@Test
	public void getJedisPool() {
		JedisPool pool = JedisPoolInstance.getInstance();

		Jedis jedis1 = pool.getResource();
		Jedis jedis2 = JedisPoolInstance.getJedis();

		try {
			logger.info("jedis1: " + jedis1);
			logger.info("jedis2: " + jedis2);
		} finally {
			logger.info("连接未关闭前，活跃的连接数：" + pool.getNumActive());
			logger.info("连接未关闭前，空闲的连接数：" + pool.getNumIdle());
			if (jedis1 != null) {
				jedis1.close();
			}
			if (jedis2 != null) {
				jedis2.close();
			}
			logger.info("连接关闭后，活跃的连接数：" + pool.getNumActive());
			logger.info("连接关闭后，空闲的连接数：" + pool.getNumIdle());
		}

	}

}
