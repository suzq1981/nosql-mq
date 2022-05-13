package com.badou.nosqlmq.redis.base.simple;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.ZParams.Aggregate;

import com.badou.nosqlmq.redis.base.pool.JedisSentinelPoolInstance;

public class RedisZSet {

	private static final Logger logger = LoggerFactory.getLogger(RedisZSet.class);

	public static void main(String[] args) {
		Jedis jedis = JedisSentinelPoolInstance.getJedis();

		jedis.zadd("icbc", 60, "刘涛");
		jedis.zadd("icbc", 60, "李健");
		jedis.zadd("icbc", 60, "苏知恩");

		jedis.zadd("ctbt", 75, "冯存钱");
		Map<String, Double> ctbtMap = new HashMap<>();
		ctbtMap.put("周瑜", 60D);
		ctbtMap.put("田慕华", 60D);
		ctbtMap.put("江欣", 60D);
		ctbtMap.put("suxiurong", 80D);
		ctbtMap.put("suxiuyun", 80D);
		ctbtMap.put("苏知恩", 85D);
		ctbtMap.put("苏知育", 85D);
		ctbtMap.put("张娟", 90D);
		ctbtMap.put("陈跃鹏", 60D);
		ctbtMap.put("周绍姜", 70D);
		jedis.zadd("ctbt", ctbtMap);

		// 返回有序集 key 中，成员 member 的 score 值。
		double score = jedis.zscore("ctbt", "张娟");
		logger.info("张娟分数：" + score);

		// 为有序集 key 的成员 member 的 score 值加上增量 increment
		jedis.zincrby("ctbt", 9, "陈跃鹏");

		// 返回有序集 key 的基数。也就是个数
		long card = jedis.zcard("ctbt");
		logger.info("功劳簿人数：" + card);

		// 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
		long count = jedis.zcount("ctbt", 80, 100);
		logger.info("功劳簿80-100分的数量：" + count);

		// 返回有序集 key 中，指定区间内的成员。其中成员的位置按 score 值递增(从小到大)来排序。
		// 具有相同 score 值的成员按字典序(lexicographical order )来排列。
		Set<String> set = jedis.zrange("ctbt", 0, 2);
		logger.info("功劳最少的3位：" + Arrays.toString(set.toArray()));

		// 其中成员的位置按 score 值递减(从大到小)来排列。 具有相同 score 值的成员按字典序的逆序排列
		set = jedis.zrevrange("ctbt", 0, 2);
		logger.info("功劳最大的3位：" + Arrays.toString(set.toArray()));

		// 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
		// 有序集成员按 score 值递增(从小到大)次序排列。
		set = jedis.zrangeByScore("ctbt", 80, 100);
		logger.info("功劳在80-100的人员：" + Arrays.toString(set.toArray()));

		// 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。
		// 有序集成员按 score 值递减(从大到小)的次序排列。
		set = jedis.zrevrangeByScore("ctbt", 100, 80);
		logger.info("功劳在100-80的人员,从大到小：" + Arrays.toString(set.toArray()));

		// 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。
		// 排名以 0 为底，也就是说， score 值最小的成员排名为 0 。
		long rank = jedis.zrank("ctbt", "陈跃鹏");
		logger.info("陈跃鹏从小到大第几位：" + rank);

		// 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。
		// 排名以 0 为底，也就是说， score 值最大的成员排名为 0 。
		rank = jedis.zrevrank("ctbt", "陈跃鹏");
		logger.info("陈跃鹏从大到小第几位：" + rank);

		// 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。
		jedis.zrem("ctbt", "陈跃鹏");

		// 移除有序集 key 中，指定排名(rank)区间内的所有成员。
		// 区间分别以下标参数 start 和 stop 指出，包含 start 和 stop 在内。
		jedis.zremrangeByRank("ctbt", 2, 3);

		// 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
		jedis.zremrangeByScore("ctbt", 60, 75);

		set = jedis.zrangeByLex("ctbt", "(su", "[y", 0, 2);
		System.out.println(Arrays.toString(set.toArray()));

		// jedis.zunionstore("zgd", "icbc", "ctbt");

		ZParams zparams = new ZParams().aggregate(ZParams.Aggregate.SUM);
		jedis.zunionstore("zgd", zparams, "icbc", "ctbt");

		jedis.close();
	}
}
