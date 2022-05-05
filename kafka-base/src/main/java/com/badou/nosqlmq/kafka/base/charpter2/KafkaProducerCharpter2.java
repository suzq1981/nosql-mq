package com.badou.mqnosql.kafka.base.charpter2;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;

import com.badou.mqnosql.kafka.base.model.User;

public class KafkaProducerCharpter2 {

	private static final String brokerList = "192.168.2.200:9092";
	private static final String heimaTopic = "heima";

	public static void main(String[] args) throws Exception {
		Properties config = new Properties();
		// 设置key序列化器
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		// 重试次数
		config.put(ProducerConfig.RETRIES_CONFIG, 10);
		// value序列化器
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				"com.badou.mqnosql.kafka.base.serializer.JsonSerializer");
		// 设置分区器，可自定义分区器，实现Partitioner接口
		config.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DefaultPartitioner.class.getName());
		// 设置集群地址
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
		//幂等性，使一个主题的一个分区中消息不会重复发送，
		//所以为了避免发送重复消息，key就非常重要，因为分区是根据key来指定的
		config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);//默认为true
		
		List<String> interceptors = new ArrayList<String>();
		interceptors.add("com.badou.mqnosql.kafka.base.interceptor.ProducerInterceptor4User");
		//配置拦截器
		config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

		KafkaProducer<String, User> producer = new KafkaProducer<String, User>(config);

		for (int i = 1; i <= 10; i++) {
			User user = User.builder().userId(i).username("Hello Kafka " + i).build();
			ProducerRecord<String, User> record = new ProducerRecord<String, User>(heimaTopic, user);
			// 同步发送
			/*
			 * Future<RecordMetadata> future = producer.send(record);
			 * RecordMetadata metadata = future.get();
			 * System.out.println("topic=" + metadata.topic());
			 * System.out.println("partition=" + metadata.partition());
			 * System.out.println("offset=" + metadata.offset());
			 */
			// 异步发送
			producer.send(record, new Callback() {
				@Override
				public void onCompletion(RecordMetadata metadata, Exception exception) {
					// System.out.println("topic=" + metadata.topic());
					// System.out.println("partition=" + metadata.partition());
					// System.out.println("offset=" + metadata.offset());
				}
			});
			TimeUnit.MILLISECONDS.sleep(200);;
		}

		producer.close();
	}
}
