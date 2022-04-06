package com.ssafy.tnt.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.tnt.entity.NewsRedisEntity;

@Repository
public interface NewsRedisRepository extends CrudRepository<NewsRedisEntity, String> {

}
