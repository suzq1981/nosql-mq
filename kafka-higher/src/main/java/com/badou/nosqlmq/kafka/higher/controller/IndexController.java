package com.badou.mqnosql.kafka.higher.controller;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@RequestMapping("/index")
	public String index() {
		return "index";
	}

	@RequestMapping("/heima/{message}")
	public String heima(@PathVariable String message) {
		kafkaTemplate.executeInTransaction(op -> {
			return op.send("heima", message);
		});
		return "ok";
	}

	@KafkaListener(groupId = "HeiMaConsumerGroupId", topicPartitions = { @TopicPartition(topic = "heima", partitions = {
			"1", "2" }) })
	public void listener(ConsumerRecord<String, String> record) {
		System.out.println("Partition=" + record.partition() + ", data=" + record.value());
	}

	@KafkaListener(groupId = "HeiMaConsumerGroupId", topicPartitions = { @TopicPartition(topic = "heima", partitions = { "0" }) })
	public void listener2(ConsumerRecord<String, String> record) {
		System.out.println("Partition=" + record.partition() + ", data=" + record.value());
	}

}
