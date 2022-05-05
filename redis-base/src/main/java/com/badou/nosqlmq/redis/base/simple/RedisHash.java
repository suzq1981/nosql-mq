package com.badou.nosqlmq.redis.base.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class RedisHash {

	private static final Logger logger = LoggerFactory.getLogger(RedisHash.class);

	public static void main(String[] args) {
		Jedis jedis = new Jedis("192.168.2.200", 6379);

		String result = null;

		// 将哈希表 hash 中域 field 的值设置为 value 。
		// 如果给定的哈希表并不存在， 那么一个新的哈希表将被创建并执行 HSET 操作。
		// 如果域 field 已经存在于哈希表中， 那么它的旧值将被新值 value 覆盖。
		jedis.hset("news", "place", "badou");
		result = jedis.hget("hkey1", "place");
		logger.info("(key=hkey1,field=place) value is " + result);

		// 当且仅当域 field 尚未存在于哈希表的情况下， 将它的值设置为 value 。
		// 如果给定域已经存在于哈希表当中， 那么命令将放弃执行设置操作。
		// 如果哈希表 hash 不存在， 那么一个新的哈希表将被创建并执行 HSETNX 命令。
		jedis.hsetnx("news", "time", "2026-6-8 7:00");
		
		boolean exist = jedis.hexists("news", "people");
		logger.info("key=news, field=people, is exist? " + exist);

		jedis.close();
	}

}
