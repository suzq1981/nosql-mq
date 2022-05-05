package com.badou.senior.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Userinfo {

	private Integer userId;
	private String username;
	private Integer age;
	
}
