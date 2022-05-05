package com.badou.nosqlmq.kafka.base.charpter1;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;

public class HelloKafkaConsumer {

	private static final String brokerList = "192.168.2.200:9092";
	private static final String heimaTopic = "heima";
	private static final String groupId = "WilliamGroup";

	public static void main(String[] args) throws Exception {
		Properties config = new Properties();
		// 设置key序列化器
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		// 重试次数
		config.put(ProducerConfig.RETRIES_CONFIG, 10);
		// value序列化器
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		// 设置集群地址
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);

		KafkaConsumer<String, String> consumer1 = new KafkaConsumer<String, String>(config);
		consumer1.subscribe(Collections.singletonList(heimaTopic),new ConsumerRebalanceListener() {
			
			@Override
			public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
				// TODO Auto-generated method stub
				
			}
		});
		// consumer1.assign(Arrays.asList(new TopicPartition(heimaTopic, 0)));

		KafkaConsumer<String, String> consumer2 = new KafkaConsumer<String, String>(config);
		consumer2.subscribe(Collections.singletonList(heimaTopic));
		// consumer2.assign(Arrays.asList(new TopicPartition(heimaTopic, 1)));

		KafkaConsumer<String, String> consumer3 = new KafkaConsumer<String, String>(config);
		consumer3.subscribe(Collections.singletonList(heimaTopic));
		// consumer3.assign(Arrays.asList(new TopicPartition(heimaTopic, 1)));

		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					ConsumerRecords<String, String> records = consumer3.poll(Duration.ofMillis(2000));
					if (records.count() > 0) {
						for (ConsumerRecord<String, String> record : records) {
							System.out.println(Thread.currentThread().getName() + ": " + record.value());
						}
					}
				}
			}
		}, "Thread-1");
		t1.start();

		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					ConsumerRecords<String, String> records = consumer2.poll(Duration.ofMillis(2000));
					if (records.count() > 0) {
						for (ConsumerRecord<String, String> record : records) {
							System.out.println(Thread.currentThread().getName() + ": " + record.value());
						}
					}
				}
			}
		}, "Thread-2");
		t2.start();

		while (true) {
			ConsumerRecords<String, String> records = consumer1.poll(Duration.ofMillis(2000));
			if (records.count() > 0) {
				for (ConsumerRecord<String, String> record : records) {
					System.out.println(Thread.currentThread().getName() + ": " + record.value());
				}
			} else {
				break;
			}
		}
		
		TimeUnit.SECONDS.sleep(10);
		consumer1.close();

		t1.join();

		consumer2.close();
		consumer3.close();
	}

}
