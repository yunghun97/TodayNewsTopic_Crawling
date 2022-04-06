package com.ssafy.tnt.service;

import java.util.List;
import java.util.Set;

import com.ssafy.tnt.entity.KeywordRedisEntity;

public interface KeywordService {
	Boolean insertKeyword(String value, double rank);
	Set<String> findKeyword();
	boolean clearKeyword();
	void copyKeyword();
}
