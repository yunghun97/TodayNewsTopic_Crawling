package com.ssafy.tnt.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsDTO {
	private long id;
	private String news_title;
	private Date news_date;
	private String news_reporter;
	private String news_company;
	private String news_content;
	private String news_thumbnail_url;
	private String news_origin_url;
	private String category;	

	@Override
	public String toString() {
		return
				"기사제목=" + news_title +
				"\n 작성일자=" + news_date +
				"\n 기자이름=" + news_reporter +
				"\n 언론사=" + news_company +
				"\n 기사내용\n" + news_content  +
				"\n news_thumbnail_url=" + news_thumbnail_url  +
				"\n news_origin_url=" + news_origin_url +
				"\n 분류=" + category;
	}
}
