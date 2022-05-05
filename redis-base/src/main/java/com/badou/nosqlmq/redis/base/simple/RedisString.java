package com.badou.nosqlmq.redis.base.simple;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class RedisString {

	private static Logger logger = LoggerFactory.getLogger(RedisString.class);

	public static void main(String[] args) {
		Jedis jedis = new Jedis("192.168.2.200", 6379);

		String result = null;

		jedis.set("RedisString1", "myvalue");

		// SET key value nx|xx ex|px time
		// 如果key为set不存在，将保留，并且设置key 5秒后过期；如果不存在不做任何操作
		jedis.set("RedisString1", "value", "nx", "ex", 10);

		// 只在键 key 不存在的情况下， 将键 key 的值设置为 value 。若键 key 已经存在， 则 setnx 命令不做任何动作。
		jedis.setnx("RedisString2", "setnx");

		// The command is exactly equivalent to the following group of commands:
		// SET + EXPIRE. The operation isatomic.
		jedis.setex("RedisString3", 6, "setex");

		// 设置过期时间
		jedis.expire("RedisString2", 5);

		// 获取key为RedisString1的值
		result = jedis.get("RedisString1");
		logger.info("RedisString1=" + result);
		result = jedis.get("RedisStringX");
		logger.info("RedisStringX=" + result);

		// 将键 key 的值设为 value ， 并返回键 key 在被设置之前的旧值。
		result = jedis.getSet("RedisString1", "newValue");
		logger.info("RedisString1 old value=" + result);

		// 返回键 key 储存的字符串值的长度
		result = String.valueOf(jedis.strlen("RedisString1"));
		logger.info("RedisString1 length=" + result);

		// 如果键 key 已经存在并且它的值是一个字符串， APPEND 命令将把 value 追加到键 key 现有值的末尾。
		// 如果 key 不存在， APPEND 就简单地将键 key 的值设为 value ， 就像执行 SET key value 一样。
		jedis.append("RedisString1", " hello!");

		// 从偏移量 offset 开始， 用 value 参数覆写(overwrite)键 key 储存的字符串值。
		// 不存在的键 key 当作空白字符串处理。
		jedis.setrange("RedisString1", 9, "world");
		jedis.setrange("RedisStringy", 8, "world");
		logger.info("RedisStringy=" + jedis.get("RedisStringy"));
		jedis.expire("RedisStringy", 10);

		// 返回键 key 储存的字符串值的指定部分， 字符串的截取范围由 start 和 end 两个偏移量决定 (包括 start 和 end
		// 在内)。
		// 负数偏移量表示从字符串的末尾开始计数， -1 表示最后一个字符， -2 表示倒数第二个字符， 以此类推。
		result = jedis.getrange("RedisString1", 3, -1);
		logger.info("getrange('RedisString1', 3, -1)=" + result);

		jedis.set("count", "0");
		jedis.incr("count");
		jedis.incrBy("count", 10);

		jedis.set("down", "100");
		jedis.decr("down");
		jedis.decrBy("down", 10);

		// 如果某个给定键已经存在， 那么 MSET 将使用新值去覆盖旧值， 如果这不是你所希望的效果， 请考虑使用 MSETNX 命令，
		// 这个命令只会在所有给定键都不存在的情况下进行设置。
		jedis.mset("count", "100", "down", "1000");

		List<String> values = jedis.mget("count", "down");
		logger.info("jedis.mget('count','down')" + Arrays.toString(values.toArray()));

		jedis.close();
	}

}
