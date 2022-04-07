package com.ssafy.tnt.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.ssafy.tnt.entity.NewsRedisEntity;
import com.ssafy.tnt.model.NewsDTO;


public interface NewsCrawlerService {
	List<NewsDTO> crawlNews() throws Exception;
	double komoran() throws IOException;
	void getTF() throws IOException;
	void insertKeyWord() throws IOException;
	void TFIDF(HashMap<String, Double> tmpTFMap) throws IOException;
	
}
