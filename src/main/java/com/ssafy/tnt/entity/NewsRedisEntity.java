package com.ssafy.tnt.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;
import lombok.Setter;

@RedisHash("news")
@Getter
@Setter
public class NewsRedisEntity {
	@Id
	private String id;
	private String title;
	private String repoter;
	private String content;
	public NewsRedisEntity(String id, String title, String repoter, String content) {
		this.id = id;
		this.title = title;
		this.repoter = repoter;
		this.content = content;
	}
}
