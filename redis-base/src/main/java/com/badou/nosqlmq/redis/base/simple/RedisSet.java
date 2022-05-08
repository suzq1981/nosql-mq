package com.badou.nosqlmq.redis.base.simple;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import com.badou.nosqlmq.redis.base.pool.JedisSentinelPoolInstance;

public class RedisSet {

	private static final Logger logger = LoggerFactory.getLogger(RedisSet.class);

	public static void main(String[] args) {

		Jedis jedis = JedisSentinelPoolInstance.getJedis();

		// 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。
		// 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。
		jedis.sadd("pure-fruit", "peach", "plum", "waxberry", "cherry", "orange", "apple", "watermelon");
		jedis.sadd("pure-man", "Lucy", "周瑜", "田慕华", "江欣", "peach", "plum", "waxberry");

		for (int i = 1; i <= 1000; i++) {
			jedis.sadd("pure-scan", "pure-" + i);
		}

		jedis.sadd("pure-scan", "Goodtime");
		jedis.sadd("pure-scan", "GoodJob");

		// 移除并返回集合中的随机元素，可指定要随机移除元素的个数
		// 如果是指定的1，那么就等价与 spop(key)
		Set<String> result = jedis.spop("pure-man", 1);
		logger.info("随机移除：" + Arrays.toString(result.toArray()));

		// 如果命令执行时，只提供了 key 参数，那么返回集合中的一个随机元素。
		// 如果提供了 count 参数，count为返回数组的元素个数。
		List<String> list = jedis.srandmember("pure-man", 2);
		logger.info("随机获取：" + Arrays.toString(list.toArray()));

		// 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
		// 如果移除的是最后一个元素，key也会被删除
		long r = jedis.srem("pure-man", "江欣");
		logger.info("是否已移除：" + r);

		// 如果 source 集合不存在或不包含指定的 member 元素，则 SMOVE 命令不执行任何操作，仅返回 0 。
		// 否则， member 元素从 source 集合中被移除，并添加到 destination 集合中去。
		jedis.smove("pure-fruit", "pure-man", "watermelon");

		long card = jedis.scard("pure-fruit");
		logger.info("pure-fruit 中元素的个数：" + card);

		// 返回集合的所有成员
		Set<String> members = jedis.smembers("pure-fruit");
		logger.info("集合中所有元素：" + Arrays.toString(members.toArray()));

		// 返回一个集合的全部成员，该集合是所有给定集合的交集。
		result = jedis.sinter("pure-fruit", "pure-man");
		logger.info("两个集合的交集：" + Arrays.toString(result.toArray()));

		// 这个命令类似于 sinter命令，但它将结果保存到 指定 集合，而不是简单地返回结果集。
		jedis.sinterstore("pure-inter", "pure-fruit", "pure-man");

		// 返回一个集合的全部成员，该集合是所有给定集合的并集。
		result = jedis.sunion("pure-fruit", "pure-man");
		logger.info("两个集合的并集：" + Arrays.toString(result.toArray()));

		// 这个命令类似于 sunion 命令，但它将结果保存到指定集合，而不是简单地返回结果集。
		jedis.sunionstore("pure-union", "pure-fruit", "pure-man");

		// 返回一个集合的全部成员，该集合是所有给定集合之间的差集。
		result = jedis.sdiff("pure-fruit", "pure-man");
		logger.info("两个集合的差集：" + Arrays.toString(result.toArray()));

		// 这个命令的作用和sdiff 类似，但它将结果保存到指定的集合，而不是简单地返回结果集。
		jedis.sdiffstore("pure-diff", "pure-fruit", "pure-man");

		String cursor = "";
		ScanParams params = new ScanParams().count(100).match("pure*");
		long total = 0;
		int time = 0;

		while (!cursor.equals("0")) {
			ScanResult<String> sr = jedis.sscan("pure-scan", cursor, params);
			cursor = sr.getStringCursor();
			total += sr.getResult().size();
			time++;
		}

		System.out.println("总记录数：" + total + ", 总查询次数：" + time);

	}

}
