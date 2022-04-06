package com.ssafy.tnt.service;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import com.ssafy.tnt.entity.KeywordEntity;
import com.ssafy.tnt.repository.KeywordRedisRepository;
import com.ssafy.tnt.repository.KeywordRepository;

@Service
public class KeywordServiceImpl implements KeywordService {
	
//	stringRedisTemplate bean은 일반적인 String 값을 key, value로 사용하는 경우 사용하면 된다.
//	redisTemplate bean은 java Object를 redis에 저장하는 경우 사용하면 된다.
	private String key = "keyword";
	private int size = 9; // 가져올 실시간 검색어 순위 개수 (10개) -> 0 ~ size까지
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	KeywordRedisRepository keywordRedisRepository;

	@Autowired
	KeywordRepository keywordRepository;
	
	@Override
	public Boolean insertKeyword(String value, double rank) {
		return redisTemplate.opsForZSet().add(this.key, value, rank);
	}

	@Override
	public Set<String> findKeyword() {
//		redisTemplate.delete(this.key); // 일괄 삭제
		return redisTemplate.opsForZSet().range(this.key, 0, 9);
	}
	
	@Override // 키워드 키 삭제
	public boolean clearKeyword() {
		try {
			
			return redisTemplate.delete(this.key);
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public void copyKeyword() {
//		long size = redisTemplate.opsForZSet().size(this.key);
		long size = 100;
		if(redisTemplate.opsForZSet().popMin(this.key)==null) return;
		for(long i=0; i <size; i++) {			
//			Set<String> set = redisTemplate.opsForZSet().range(this.key, i, i);
			TypedTuple<String> tuple = redisTemplate.opsForZSet().popMin(this.key);			
			System.out.println("밸류 "+ tuple.getValue()+"  스코어 "+tuple.getScore());
			KeywordEntity keywordEntity = new KeywordEntity(tuple.getValue(), tuple.getScore() * -1, null);
			keywordRepository.save(keywordEntity);
		}
		return;
	}
}
