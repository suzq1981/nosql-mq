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
import org.apache.kafka.common.serialization.StringSerializer;

import com.badou.mqnosql.kafka.base.model.User;
import com.badou.mqnosql.kafka.base.serializer.JsonSerializer;

public class KafkaProducerTransaction {

	private static final String brokerList = "192.168.2.200:9092,192.168.2.200:9093,192.168.2.200:9094";
	private static final String heimaTopic = "heima";
	private static final String transactionId = "TransactionId";

	public static void main(String[] args) throws Exception {
		Properties config = new Properties();
		// 设置key序列化器
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		// 重试次数
		config.put(ProducerConfig.RETRIES_CONFIG, 10);
		// value序列化器
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
		// 设置分区器，可自定义分区器，实现Partitioner接口
		config.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DefaultPartitioner.class.getName());
		// 设置集群地址
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
		// 幂等性，使一个主题的一个分区中消息不会重复发送，
		// 所以为了避免发送重复消息，key就非常重要，因为分区是根据key来指定的
		config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);// 默认为true

		config.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionId);

		List<String> interceptors = new ArrayList<String>();
		interceptors.add("com.badou.mqnosql.kafka.base.interceptor.ProducerInterceptor4User");
		// 配置拦截器
		config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

		KafkaProducer<String, User> producer = new KafkaProducer<String, User>(config);

		// 初始化事务
		producer.initTransactions();

		for (int i = 1; i <= 10; i++) {
			User user = User.builder().userId(i).username("Hello Kafka " + i).build();
			ProducerRecord<String, User> record = new ProducerRecord<String, User>(heimaTopic, user);

			try {
				// 开启事务
				producer.beginTransaction();
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
						// System.out.println("partition=" +
						// metadata.partition());
						// System.out.println("offset=" + metadata.offset());
					}
				});

				if (i % 2 == 0) {
					throw new Exception("i%2 == 0");
				}

				producer.commitTransaction();
			} catch (Exception ex) {
				producer.abortTransaction();
			}
			TimeUnit.MILLISECONDS.sleep(200);
		}

		producer.close();
	}

}
