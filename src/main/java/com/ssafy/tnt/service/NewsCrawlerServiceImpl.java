package com.ssafy.tnt.service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.swing.filechooser.FileSystemView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.tnt.model.NewsDTO;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

@Service
public class NewsCrawlerServiceImpl implements NewsCrawlerService {
	private static String NAVER_NEWS_URL = "https://news.naver.com/main/list.naver?mode=LPOD&mid=sec&oid=";
	private static final String[] COMPANY_CODES = { "032", "005", "020", "021", "081", "022", "023", "025", "028", "469",
			"421", "003", "001", "422", "449", "215", "437", "056", "214", "057", "055", "374", "448", "052", "009",
			"008", "648", "011", "277", "018", "366", "123", "014", "015", "016", "079", "629", "119", "417", "006",
			"031", "047", "002", "138", "029", "293", "030", "092" };
	private static final String[] COMPANY_CODES11 = { "032" };
	private static HashMap<String, Double> IDFMap, resultMap;
	private static HashMap<String, Boolean> visitMap;
	private static double newsCount;
	private static double TFIDFValue = 0.3;
	
	private static Set<String> blackListMap = new HashSet<>(Arrays.asList(".net","647","분기","뉴스1","뉴시스",".kr",".co"));		
	// 서버 상대경로 얻을 때 사용
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private NewsService newsService;
	
	@Autowired
	private KeywordService keywordService;
	
	@Override
	public double crawlNews() throws Exception {
		// TODO Auto-generated method stub
		long beforeTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c1 = Calendar.getInstance();
		String strToday = sdf.format(c1.getTime());
		newsCount = 0;		
		
		String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
		String basePath = "/news/news.txt";
		FileWriter fileWriter = new FileWriter(rootPath+basePath);		
		//언론사 코드 만큼
		next_news:for (String company_code : COMPANY_CODES) {
			List<String> urllist = new ArrayList<String>();
			Document doc = null ;
			try {
				doc = Jsoup.connect(NAVER_NEWS_URL+company_code+"&listType=title&date="+strToday).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue next_news;
			}
			//1페이지 크롤링
			Elements news_elements = doc.select("ul.type02>li>a");
			for (Element news_element : news_elements) {
				String url = news_element.attr("href");
				System.out.println(url);
				urllist.add(url);
			}
			//페이지수 확인
			Elements page_elements = doc.select("div.paging>a");
			//페이지수만큼 재탐색
			for (int i = 0; i < page_elements.size(); i++) {
				try {
					doc = Jsoup.connect(NAVER_NEWS_URL+company_code+"&listType=title&date="+strToday+"&page="+page_elements.get(i).text()).get();
					news_elements = doc.select("ul.type02>li>a");
					//각 페이지의 뉴스 기사만큼 url 추가
					for (Element news_element : news_elements) {
						String url = news_element.attr("href");
//						System.out.println(url);
						urllist.add(url);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			SimpleDateFormat transFormat = new SimpleDateFormat("yyyy.MM.dd.");	
			//누적된 url 탐색하며 크롤링 시작
			for (String url : urllist) {
				NewsDTO news = new NewsDTO();
				Document news_doc = Jsoup.connect(url).get();	
				String no = url.substring(url.length()-10, url.length());
				String title = news_doc.select("#articleTitle").text();
				if(title.equals("")){
					title = news_doc.select("h4.title").text();
				}
				if(title.equals("")){
					title = news_doc.select("h2.end_tit").text();
				}
				if(title.equals("")){
					title = news_doc.select("h3#articleTitle").text();
				}
				Date date = null;
	            if(!news_doc.select("div.sponsor>span.t11").text().equals("")){
//	               date = transFormat.parse((String)news_doc.select("div.sponsor>span.t11").get(0).text().substring(0,19).replaceAll("[오전,오후]","").replaceAll("[ ]",""));
	            	StringTokenizer st = new StringTokenizer(news_doc.select("div.sponsor>span.t11").get(0).text(),".: ");
	            	int year = Integer.parseInt(st.nextToken())-1900;
	            	int month = Integer.parseInt(st.nextToken())-1;
	            	int day = Integer.parseInt(st.nextToken());
	            	int hour = 0;
	            	if(st.nextToken().equals("오전")){
	            		int tmpHour = Integer.parseInt(st.nextToken());
	            		if(tmpHour==12) hour = 12-tmpHour;
	            		else hour = tmpHour;
	            	}else {
	            		int tmpHour = Integer.parseInt(st.nextToken());
	            		if(tmpHour==12) hour = 12;
	            		else hour = 12+tmpHour;
	            	}
	            		            	
	            	int minute = Integer.parseInt(st.nextToken());
	            	date = new Date(year, month, day, hour, minute);
	            }else {
	               date = sdf.parse(strToday);
	            }
				//기자이름 크롤링 -> 이메일포함이라면 이메일 제거
				String reporter = news_doc.select("div.journalistcard_summary_name").text();
				if(reporter.equals("")) {
					reporter = news_doc.select("div.byline>p.b_text").text();
				}
				if(reporter.equals("")) {
					reporter = news_doc.select("p.source>p.byline").text();
				}
				if(reporter.equals("")){
					reporter = news_doc.select("p.byline_p>span").text();
				}
				if(reporter.equals("")){
					reporter = news_doc.select("div.profile_info>a>div.name").text();
				}
				if(reporter.contains("@")) {
					for (int i = 0; i < reporter.length(); i++) {
						if(Pattern.matches("^[a-zA-Z]*$", Character.toString(reporter.charAt(i)))) {
							reporter=reporter.substring(0,i);
							break;
						}						
					}
				}
				String company = news_doc.select("div.press_logo>a>img").attr("alt");
				if(company.equals("")){
					company=news_doc.select("span#pressLogo>a>img").attr("alt");
				}
				String content = news_doc.select("div#articleBodyContents").text();
				if(content.equals("")){
					content = news_doc.select("div#newsEndContents").text();
				}
				if(content.equals("")){
					content = news_doc.select("div#articeBody").text();
				}
				if(content.equals("")){
					content = news_doc.select("div#dic_area").text();
				}

				String thumbnail_url=null;
				if(news_doc.select("span.end_photo_org>img")!=null) {
					thumbnail_url = news_doc.select("span.end_photo_org>img").attr("src");					
				}

				String category = news_doc.select("em.guide_categorization_item").text();

				news.setNews_title(title);
				news.setNews_date(date);
				news.setNews_reporter(reporter);
				news.setNews_company(company);
				news.setNews_content(content);
				news.setCategory(category);				
				if(thumbnail_url!=null) {
					news.setNews_thumbnail_url(thumbnail_url);
				}
				news.setNews_origin_url(url);
				if(news.getCategory()==null||news.getCategory().equals("")) news.setCategory("기타");
				fileWriter.write(news.toString()+"\r\n");
				newsCount++;
				newsService.NewsInsert(news);
//
//				return null; //한개씩만
//				System.out.println(news.toString());				
			}
		}	// 반복문 끝
		fileWriter.close();
		rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
		basePath = "/news/newsCount.txt";
		fileWriter = new FileWriter(rootPath+basePath);
		fileWriter.write(String.valueOf(newsCount));
		fileWriter.close();
		long afterTime = System.currentTimeMillis();
		long secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
		System.out.println("시간차이(m) : "+secDiffTime);
		return newsCount;
	}
	public double komoran() throws IOException 
	{
		Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
		IDFMap = new HashMap<>();
		visitMap = new HashMap<>();
		List<String> nounlist = new ArrayList<>();
		String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
		String newsPath = "/news/news.txt";
		String basePath = "/news/keyword.txt";		
		BufferedReader reader = new BufferedReader(new FileReader(rootPath+newsPath));
		FileWriter writer = new FileWriter(rootPath+basePath);
		String str;
		boolean flag = false;
		while((str=reader.readLine())!=null){
			if(str.contains("기사내용")){
				flag = true;
				continue;
			}
			if(flag&&!str.equals("")){
				visitMap.clear();
				KomoranResult analyzeResultList = komoran.analyze(str);
				try {
					List<Token> tokenList = analyzeResultList.getTokenList();
					if(!tokenList.isEmpty()) {
						for (Token token : tokenList) {
							if(token.getPos().equals("NNP")||token.getPos().equals("NNG")){
								nounlist.add(token.getMorph().toString());
								if(visitMap.containsKey(token.getMorph().toString())) {
									continue;
								}else{
									visitMap.put(token.getMorph().toString(), true);
									if(IDFMap.containsKey(token.getMorph().toString())) {
										IDFMap.put(token.getMorph().toString(), IDFMap.get(token.getMorph().toString())+1);
									}else {
										IDFMap.put(token.getMorph().toString(), 1.0);
									}
								}								
							}
						}
					}
					String keyword = nounlist.toString();
					keyword=keyword.substring(1,keyword.length());
					keyword=keyword.substring(0,keyword.length()-1).replaceAll(","," ");
					keyword+="\n";
					writer.write(keyword);  //형태소 분석					
//					System.out.println(nounlist.toString());
					nounlist.clear();
				} catch (Exception e) {
					continue;
				}finally {
					flag=false;
				}
			}
		}
		writer.close();
		reader.close();		
		return newsCount;
	}	
	public void getTF() throws IOException {
		resultMap = new HashMap<>();		
		String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
		String newsPath = "/news/keyword.txt";
		BufferedReader reader = new BufferedReader(new FileReader(rootPath+newsPath));		
		String str;
		HashMap<String, Double> TFMap = new HashMap<>();		
		StringTokenizer st;
		
		while((str=reader.readLine())!=null){
			st = new StringTokenizer(str);
			while(st.hasMoreTokens()) {
				String input = st.nextToken();
				if(TFMap.containsKey(input)) {
					TFMap.put(input, TFMap.get(input)+1);
				}else{
					TFMap.put(input,1.0);
				}
			}
			TFIDF(TFMap);
			TFMap.clear();
		}
		IDFMap.clear();
		TFMap.clear();
		reader.close();	
		insertKeyWord();
		return;		
	}
	
	@Override
	public void insertKeyWord() throws IOException {
		System.out.println("진입");
		String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
		String basePath = "/news/newsTDIDF.txt";
		FileWriter fileWriter = new FileWriter(rootPath+basePath);
		keywordService.copyKeyword();
		keywordService.clearKeyword();
		
		for(Map.Entry<String, Double> map :  resultMap.entrySet()) {			
			keywordService.insertKeyword(map.getKey(), map.getValue()*-1);			
			fileWriter.write(map.getKey()+" : "+map.getValue()+"\n");
		}		
		fileWriter.close();		
		return;		
	}
	
	@Override
	public void TFIDF(HashMap<String, Double> tmpTFMap) throws IOException {			
		for(Map.Entry<String, Double> map :  tmpTFMap.entrySet()) {
			double TF = map.getValue() / tmpTFMap.size();
			if(IDFMap.get(map.getKey())==null) continue;
			double IDF = Math.log(newsCount/IDFMap.get(map.getKey()));
			double TFIDF = TF * IDF;			
			System.out.println("TFIDF 값 : "+TFIDF);
			if(TFIDF<TFIDFValue) continue; // TFIDF 조건 넘어갈 시 continue
			if(blackListMap.contains(map.getKey())) continue;
			if(resultMap.containsKey(map.getKey())) { // 이미 있는 경우
				resultMap.put(map.getKey(), resultMap.get(map.getKey())+1);
			}else {
				resultMap.put(map.getKey(), 1.0);
			}
		}		
		return;
	}
	public class TFIDFImpl{
		
	    //list : 키워드 리스트 : 기사 본문 하나를 필터링한 값
	    //word : 리스트에서 뽑은 tf-idf 값을 구하고 싶은 단어
	    
	    public double tf(List<String> list, String word) {
	        double result = 0;
	        for (String targetWord : list)
	            if (word.equalsIgnoreCase(targetWord)) result++;
	        return result / list.size();
	    }
	    
	    //lists : 모든 기사를 필터링한 키워드 리스트
	    //word : 값을 구하고 싶은 단어
	    
	    public double idf(List<List<String>> lists, String word) {
	        double n = 0;
	        for (List<String> list : lists) {
	            for (String targetWord : list) {
	                if (word.equalsIgnoreCase(targetWord)) {
	                    n++; break;
	                }
	            }
	        }
	        return Math.log(lists.size() / n);
	    }
	    
	    public double tfidf(List<String> list, List<List<String>> lists, String word) {
	        return tf(list, word) * idf(lists, word);
	    }
	}
}
