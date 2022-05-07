package com.badou.nosqlmq.redis.base.simple;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import com.badou.nosqlmq.redis.base.pool.JedisSentinelPoolInstance;

public class RedisHash {

	private static final Logger logger = LoggerFactory.getLogger(RedisHash.class);

	public static void main(String[] args) {
		Jedis jedis = JedisSentinelPoolInstance.getJedis();

		String result = null;

		// 将哈希表 hash 中域 field 的值设置为 value 。
		// 如果给定的哈希表并不存在， 那么一个新的哈希表将被创建并执行 HSET 操作。
		// 如果域 field 已经存在于哈希表中， 那么它的旧值将被新值 value 覆盖。
		jedis.hset("news", "people", "William");
		jedis.hset("news", "place", "badou");
		result = jedis.hget("hkey1", "place");
		logger.info("(key=hkey1,field=place) value is " + result);

		// 当且仅当域 field 尚未存在于哈希表的情况下， 将它的值设置为 value 。
		// 如果给定域已经存在于哈希表当中， 那么命令将放弃执行设置操作。
		// 如果哈希表 hash 不存在， 那么一个新的哈希表将被创建并执行 HSETNX 命令。
		jedis.hsetnx("news", "time", "2026-6-8 7:00");

		boolean exist = jedis.hexists("news", "people");
		logger.info("key=news, field=people, is exist? " + exist);

		// 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
		// jedis.hdel("news", "people", "time");

		// 返回哈希表 key 中域的数量
		long length = jedis.hlen("news");
		logger.info("key=news的域数量: " + length);

		// 为哈希表 key 中的域 field 的值加上增量 increment,
		// 增量也可以为负数，相当于对给定域进行减法操作。
		jedis.hincrBy("news", "count", 10);

		Map<String, String> map = new HashMap<>();
		map.put("author", "Godson");
		map.put("publish", "CN");

		// 同时将多个 field-value (域-值)对设置到哈希表 key 中。
		// 此命令会覆盖哈希表中已存在的域。
		// 如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
		jedis.hmset("news", map);

		// 返回哈希表 key 中，一个或多个给定域的值。
		// 如果给定的域不存在于哈希表，那么返回一个 nil 值。
		// 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表
		List<String> list = jedis.hmget("news", "place", "author", "people", "wlc");
		logger.info(Arrays.toString(list.toArray()));

		// 返回哈希表 key 中的所有域。
		Set<String> set = jedis.hkeys("news");
		logger.info("keys: " + Arrays.toString(set.toArray()));

		// 返回哈希表 key 中所有域的值。
		List<String> values = jedis.hvals("news");
		logger.info("values: " + Arrays.toString(values.toArray()));
		for (String v : values) {
			if (!v.startsWith("v")) {
				System.out.println("kkkkkkkkk=" + v);
			}
		}
		System.out.println("ddddddddd=" + values.size());

		// 返回哈希表 key 中，所有的域和值。
		Map<String, String> allMap = jedis.hgetAll("news");
		logger.info(allMap.toString());

		for (int i = 1; i <= 1000; i++) {
			jedis.hsetnx("news", "k" + i, "v" + i);
		}
		
		//每次返回来就100左右，有时会超过100
		ScanParams params = new ScanParams().count(100).match("k*");
		ScanResult<Entry<String, String>> scan;
		long totalSize = 0;
		String cursor = "";
		
		while (!cursor.equals("0")) {
			scan = jedis.hscan("news", cursor, params);
			cursor = scan.getStringCursor();
			totalSize += scan.getResult().size();
		}
		System.out.println("总记录数=" + totalSize);

		jedis.close();
	}
}
