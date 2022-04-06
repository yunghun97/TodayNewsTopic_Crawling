package com.ssafy.tnt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.tnt.entity.NewsRedisEntity;
import com.ssafy.tnt.repository.NewsRedisRepository;

@Service
public class NewsRedisServiceImpl implements NewsRedisService{
	@Autowired
	NewsRedisRepository newsRedisRepository;
	
	@Override
	public NewsRedisEntity RedisTest() {
		NewsRedisEntity news =new NewsRedisEntity("1번", "기사제목2", "권영현2", "redis 테스트입니다.2"); 
		newsRedisRepository.save(news);
		return news;
	}

	@Override
	public List<NewsRedisEntity> RedisGetTest() {
		List<NewsRedisEntity> list = (List<NewsRedisEntity>) newsRedisRepository.findAll();
		return list;
	}

}
