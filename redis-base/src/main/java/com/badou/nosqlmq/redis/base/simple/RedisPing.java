package com.badou.nosqlmq.redis.base.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class RedisPing {

	private static final Logger logger = LoggerFactory.getLogger(RedisPing.class);

	public static void main(String[] args) throws Exception {
		Jedis jedis = new Jedis("192.168.2.200", 6379);
		logger.info("ping: " + jedis.ping());

		jedis.close();
	}

}
