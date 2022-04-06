package com.ssafy.tnt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.tnt.entity.KeywordRedisEntity;

@Repository
public interface KeywordRedisRepository extends CrudRepository<KeywordRedisEntity, String>{
}
