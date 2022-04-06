package com.ssafy.tnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.tnt.entity.KeywordEntity;

@Repository
public interface KeywordRepository extends JpaRepository<KeywordEntity, String>{

}
