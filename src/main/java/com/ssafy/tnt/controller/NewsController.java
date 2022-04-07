package com.ssafy.tnt.controller;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.tnt.service.NewsCrawlerService;
import com.ssafy.tnt.service.NewsRedisService;

@RequestMapping("/api/news")
@RestController
public class NewsController {

	private String authKey = "ssafyBig6ix";

	@Autowired
	NewsCrawlerService newsCrawlerService;
	@Autowired
	NewsRedisService newsRedisService;

	@PostMapping("/craw")
	public ResponseEntity<String> NewsCrawling(@RequestHeader(value = "Authorization") String auth) {
		double count = 0;
		if (possibleKeyCheck(auth)) {
			try {
				newsCrawlerService.crawlNews();
				count = newsCrawlerService.komoran();
				newsCrawlerService.getTF();
				
				return new ResponseEntity<>("Success : 크롤링한 뉴스 기사 개수 : " + count, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<>("Error : 서버 오류 : ", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else
			return new ResponseEntity<>("올바르지 않는 Header 값 : ", HttpStatus.UNAUTHORIZED);
	}

//	@PostMapping("/redis")
//	public ResponseEntity<NewsRedisEntity> RedisTest() {
//		return new ResponseEntity<>(newsRedis.RedisTest(), HttpStatus.OK);
//	}
//
//	@GetMapping("/redis")
//	public ResponseEntity<List<NewsRedisEntity>> RedisGetTest() {
//		return new ResponseEntity<>(newsRedis.RedisGetTest(), HttpStatus.OK);
//	}

	private boolean possibleKeyCheck(String key) {

		if (new String(Base64.getDecoder().decode(key)).equals(this.authKey))
			return true;
		else
			return false;
	}
	
}
