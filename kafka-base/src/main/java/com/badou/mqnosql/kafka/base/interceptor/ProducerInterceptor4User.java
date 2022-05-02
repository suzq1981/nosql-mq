package com.badou.mqnosql.kafka.base.interceptor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.badou.mqnosql.kafka.base.model.User;

public class ProducerInterceptor4User implements ProducerInterceptor<String, User> {

	// 消息发送成功数
	private volatile AtomicLong succAccount = new AtomicLong(0);
	// 消息发送失败数
	private volatile AtomicLong failAccount = new AtomicLong(0);

	@Override
	public void configure(Map<String, ?> configs) {

	}

	@Override
	public ProducerRecord<String, User> onSend(ProducerRecord<String, User> record) {
		User value = record.value();
		value.setUserId(value.getUserId() + 10);
		return new ProducerRecord<String, User>(record.topic(), record.partition(), record.timestamp(), record.key(),
				value, record.headers());
	}

	@Override
	public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
		if (exception == null) {
			succAccount.incrementAndGet();
		} else {
			failAccount.incrementAndGet();
		}
	}

	@Override
	public void close() {
		System.out.println("消息发送成功数：" + succAccount.intValue() + ", 消息发送失败数：" + failAccount.intValue());
	}

}
