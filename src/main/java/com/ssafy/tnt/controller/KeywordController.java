package com.ssafy.tnt.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.tnt.entity.KeywordRedisEntity;
import com.ssafy.tnt.service.KeywordService;

@RequestMapping("/keyword")
@RestController
public class KeywordController {
	@Autowired
	KeywordService keywordService;
	
	@PostMapping()
	public ResponseEntity<String> registKeyword(@RequestBody Map<String, String> map) {
		String value = map.get("value");
		double rank = Double.parseDouble(map.get("rank"));
		if(keywordService.insertKeyword(value,rank)) {
			return new ResponseEntity<>("Success : 키워드 입력 성공",HttpStatus.OK);
		}else {
			return new ResponseEntity<>("Fail : 키워드 입력 실패",HttpStatus.INTERNAL_SERVER_ERROR);
		}
			
	}
	// 최대 10개 조회하기
	@GetMapping()
	public ResponseEntity<HashMap<String, Double>> findKeyword() {
		return (ResponseEntity<HashMap<String, Double>>) keywordService.findKeyword();
//		return new ResponseEntity<>(keywordService.findKeyword(), HttpStatus.OK);
	}
	
	@GetMapping("/test")
	public void Test() {
		keywordService.clearKeyword();
	}
}
