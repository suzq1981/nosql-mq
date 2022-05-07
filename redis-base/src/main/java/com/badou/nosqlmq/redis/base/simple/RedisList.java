package com.badou.nosqlmq.redis.base.simple;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.Jedis;

import com.badou.nosqlmq.redis.base.pool.JedisSentinelPoolInstance;

public class RedisList {

	private static final Logger logger = LoggerFactory.getLogger(RedisList.class);

	public static void main(String[] args) {

		Jedis jedis = JedisSentinelPoolInstance.getJedis();

		jedis.del("plan", "plan2");

		// 将一个或多个值 value 插入到列表 key 的表头
		// 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头
		jedis.lpush("plan", "AM 8点看新闻", "AM 7点起床");
		// 将值 value 插入到列表 key 的表头，当且仅当 key 存在并且是一个列表。
		jedis.lpushx("plan", "AM 6:30醒来");
		// 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
		// 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾
		jedis.rpush("plan", "AM 9点学习", "Am 10点学习", "Am 11点学习");
		// 将值 value 插入到列表 key 的表尾，当且仅当 key 存在并且是一个列表
		jedis.rpushx("plan", "AM 12点午饭", "午休30分", "午休分");

		// 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定
		// 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1
		// 表示列表的第二个元素，以此类推。
		// 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
		List<String> list = jedis.lrange("plan", 0, -1);
		logger.info("所有数据：" + Arrays.toString(list.toArray()));
		list = jedis.lrange("plan", -3, -1);
		logger.info("倒数第3个-倒数第1个：" + Arrays.toString(list.toArray()));

		// 移除并返回列表 key 的头元素。
		String value = jedis.lpop("plan");
		logger.info("移除列表头元素：" + value);

		// 移除并返回列表 key 的尾元素。
		value = jedis.rpop("plan");
		logger.info("一处列表尾元素：" + value);

		value = jedis.rpoplpush("plan", "plan2");
		logger.info("转移：plan->plan2 [" + value + "]");

		jedis.rpush("plan", "喝茶10分", "喝茶10分", "喝茶10分");
		jedis.rpush("plan", "PM 2点学习");
		jedis.rpush("plan", "PM 3点学习");
		jedis.rpush("plan", "PM 4点锻炼");
		jedis.rpush("plan", "PM 5点锻炼");
		jedis.rpush("plan", "PM 6点晚饭");
		jedis.rpush("plan", "PM 7点娱乐");
		jedis.rpush("plan", "PM 8点娱乐");

		// 根据参数 count 的值，移除列表中与参数 value 相等的元素
		// count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。
		// count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。
		// count = 0 : 移除表中所有与 value 相等的值。
		jedis.lrem("plan", -3, "喝茶10分");

		// 返回列表 key 的长度。
		long length = jedis.llen("plan");
		logger.info("列表长度：" + length);

		// 返回列表 key 中，下标为 index 的元素。也就是第(index+1)个
		value = jedis.lindex("plan", 3);
		logger.info("index为3的元素：" + value);

		jedis.linsert("plan", LIST_POSITION.BEFORE, "PM 2点学习", "午休");
		list = jedis.lrange("plan", 0, -1);
		logger.info("所有数据：" + Arrays.toString(list.toArray()));

		jedis.lset("plan", 6, "午休1小时30分");

		jedis.ltrim("plan", 1, -3);
		list = jedis.lrange("plan", 0, -1);
		logger.info("trim 之后的内容：" + Arrays.toString(list.toArray()));

		list = jedis.brpop(10, "plan");
		logger.info("brpop内容：" + Arrays.toString(list.toArray()));
		jedis.close();
	}
}
