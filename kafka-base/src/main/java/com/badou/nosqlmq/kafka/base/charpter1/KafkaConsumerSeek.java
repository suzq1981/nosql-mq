package com.badou.nosqlmq.kafka.base.charpter1;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;

public class KafkaConsumerSeek {

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
		// 设置offset自动提交为手动提交
		config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);// 默认为true

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(config);
		//consumer.subscribe(Collections.singletonList(heimaTopic));
		TopicPartition tp0 = new TopicPartition(heimaTopic, 0);
		consumer.assign(Arrays.asList(tp0));

		consumer.seek(tp0, 247);
		
		long lastConsumeOffset = -1;

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(2000));
			if (records.isEmpty()) {
				break;
			}
			List<ConsumerRecord<String, String>> partitionRecords = records.records(tp0);
			lastConsumeOffset = partitionRecords.get(partitionRecords.size() - 1).offset();

			consumer.commitAsync();
			for (ConsumerRecord<String, String> record : partitionRecords) {
				System.out.println(record.partition()+ " : " + record.value());
			}
		}

		System.out.println("consumed offset is " + lastConsumeOffset);
		OffsetAndMetadata offsetAndMetadata = consumer.committed(tp0);
		System.out.println("commited offset is " + offsetAndMetadata.offset());
		long position = consumer.position(tp0);
		System.out.println("the offset of the next record is " + position);

		consumer.close();
	}
}
