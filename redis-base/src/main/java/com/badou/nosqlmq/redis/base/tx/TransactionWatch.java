package com.badou.nosqlmq.redis.base.tx;

import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.badou.nosqlmq.redis.base.pool.JedisPoolInstance;

public class TransactionWatch {

	public static void main(String[] args) {
		Jedis jedis = JedisPoolInstance.getJedis();

		String costTimeStr = jedis.get("tx.costTime");
		String salaryStr = jedis.get("tx.salary");

		int costTime = costTimeStr == null ? 0 : Integer.parseInt(costTimeStr);
		int salary = salaryStr == null ? 0 : Integer.parseInt(salaryStr);
		
		Transaction tx = null;
		
		try {
			if (costTime < 10) {
				jedis.watch("tx.costTime");
				TimeUnit.SECONDS.sleep(10);
				tx = jedis.multi();
				tx.set("tx.costTime", String.valueOf(costTime + 1));
				tx.set("tx.salary", String.valueOf(salary + 1000));
				tx.exec();
			}
		} catch (Exception ex) {
			if (tx != null) {
				tx.discard();
			}
		}

		jedis.close();
	}
}
