package com.ssafy.tnt.service;

import java.io.IOException;
import java.util.HashMap;


public interface NewsCrawlerService {
	double crawlNews() throws Exception;
	double komoran() throws IOException;
	void getTF() throws IOException;
	void insertKeyWord() throws IOException;
	void TFIDF(HashMap<String, Double> tmpTFMap) throws IOException;
	
}
