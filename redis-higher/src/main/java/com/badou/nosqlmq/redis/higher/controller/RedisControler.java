package com.badou.nosqlmq.redis.higher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisControler {

	@Autowired
	private ValueOperations valueOperations;

	@RequestMapping("/stip")
	public String stip(String words) {
		valueOperations.set("badou.tip", words);
		return "OK";
	}

	@RequestMapping("/gtip")
	public String gtip() {
		String tip = (String) valueOperations.get("badou.tip");
		return tip;
	}

}
