package com.ssafy.tnt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.tnt.entity.NewsEntity;
import com.ssafy.tnt.model.NewsDTO;
import com.ssafy.tnt.repository.NewsRepository;

@Service
public class NewsServiceImpl implements NewsService{

	@Autowired
	NewsRepository newsRepository;
	
	@Override
	public void NewsInsert(NewsDTO newsDto) {
		NewsEntity news = new NewsEntity(newsDto.getId(), newsDto.getNews_title(),
			newsDto.getCategory(), newsDto.getNews_date(), newsDto.getNews_reporter(), 
			newsDto.getNews_content(), newsDto.getNews_company(), newsDto.getNews_thumbnail_url(), newsDto.getNews_origin_url());
		newsRepository.save(news);
	}

}
