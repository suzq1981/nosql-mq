package com.badou.mqnosql.kafka.base.charpter1;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;

public class HelloKafkaConsumerSimpleBalance {

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

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(config);

		Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
		consumer.subscribe(Collections.singletonList(heimaTopic), new ConsumerRebalanceListener() {

			/**
			 * 该方法会在再均衡之前和消费者停止读取之后被调用 如果在该方法提交指定分区的偏移量，则在下一个消费者就可以获得读取的偏移量
			 */
			@Override
			public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
				consumer.commitAsync(offsets, new OffsetCommitCallback() {
					@Override
					public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
					}
				});
				System.out.println("提交onPartitionsRevoked");
			}

			/**
			 * 该方法会在再均衡之后和消费者读取之前被调用
			 */
			@Override
			public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
				if (partitions.size() > 0) {
					long committedOffset = -1;
					for (TopicPartition tp : partitions) {
						// 获取该分区已提交的偏移量
						committedOffset = consumer.committed(tp).offset();
						consumer.seek(tp, committedOffset);
					}
					System.out.println("读取指定分区");
				}
			}
		});

		int times = 0;

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(2000));
			times++;
			if (records.count() > 0) {
				Iterator<ConsumerRecord<String, String>> iterator = records.iterator();
				while (iterator.hasNext()) {
					ConsumerRecord<String, String> record = iterator.next();
					offsets.put(new TopicPartition(record.topic(), record.partition()),
							new OffsetAndMetadata(record.offset() + 1));

					System.out.println("partitioin=" + record.partition() + ", offset=" + record.offset() + ", value="
							+ record.value());
				}
			}
			if (times > 105) {
				break;
			}
		}

		consumer.close();
	}

}
