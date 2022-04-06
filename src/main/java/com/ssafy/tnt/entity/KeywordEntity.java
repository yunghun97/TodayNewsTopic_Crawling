package com.ssafy.tnt.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="keyword")
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class KeywordEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="keyword_no")
	private long no;
	
	@Column(name="keyword_word")
	private String word;
	
	@Column(name="keyword_count")
	private Double count;
	
	@Column(name="keyword_date")
	private LocalDate date;

	public KeywordEntity(String word, double count, LocalDate date) {		
		this.word = word;
		this.count = count;
		this.date = date;
	}
	
	@PrePersist
	public void createAt() {
		this.date = LocalDate.now();
	}
}
