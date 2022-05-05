package com.badou.nosqlmq.kafka.base.charpter1;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;

public class KafkaSeekToEnd {

	private static final String brokerList = "192.168.2.200:9092";
	private static final String heimaTopic = "heima";
	private static final String groupId = "MyConsumerGroup";

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
		// 设置客户端id
		config.put(ConsumerConfig.CLIENT_ID_CONFIG, "Badou-Kafka");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(config);
		consumer.subscribe(Collections.singletonList(heimaTopic));

		Set<TopicPartition> tpSet = consumer.assignment();

		while (tpSet.size() == 0) {
			consumer.poll(Duration.ofMillis(200));
			tpSet = consumer.assignment();
		}

		Map<TopicPartition, Long> offsets = consumer.endOffsets(tpSet);
		for (TopicPartition tp : tpSet) {
			//consumer.seek(tp, offsets.get(tp) + 1);
			consumer.seek(tp, offsets.get(tp));
		}

//		for (TopicPartition tp : tpSet) {
//			consumer.seek(tp, 230);
//		}

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3));
			if (records.isEmpty()) {
				break;
			}

			consumer.commitAsync();
			for (ConsumerRecord<String, String> record : records) {
				System.out.println(record.partition() + " : " + record.value());
			}
		}

		consumer.close();
	}

}
