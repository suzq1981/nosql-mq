package com.badou.nosqlmq.redis.base.tx;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.badou.nosqlmq.redis.base.pool.JedisPoolInstance;

public class TransactionSimple {

	public static void main(String[] args) {
		Jedis jedis = JedisPoolInstance.getJedis();
		Transaction tx = jedis.multi();
		tx.set("tx.salary", "10000");
		tx.set("tx.costTime", "8");
		tx.exec();
		
		Transaction t = jedis.multi();
		t.set("tx.salary", "20000");
		t.set("tx.costTime", "14");
		t.discard();
		
		jedis.close();
	}
}
