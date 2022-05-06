package com.badou.nosqlmq.redis.base.pool;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class TestJedisSentinelPool {
	
	private static final Logger logger = LoggerFactory.getLogger(TestJedisSentinelPool.class);

	@Test
	public void sentinel(){
		Jedis jedis = JedisSentinelPoolInstance.getJedis();
		jedis.hset("news", "place", "badou-djt");
		
		String result = null;
		
		result = jedis.hget("hkey1", "place");
		logger.info("(key=hkey1,field=place) value is " + result);

		// 当且仅当域 field 尚未存在于哈希表的情况下， 将它的值设置为 value 。
		// 如果给定域已经存在于哈希表当中， 那么命令将放弃执行设置操作。
		// 如果哈希表 hash 不存在， 那么一个新的哈希表将被创建并执行 HSETNX 命令。
		jedis.hsetnx("news", "time", "2026-6-8 7:00");
	}
}
