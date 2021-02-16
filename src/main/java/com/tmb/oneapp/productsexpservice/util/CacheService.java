package com.tmb.oneapp.productsexpservice.util;

import com.tmb.common.logger.TMBLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
    private static final TMBLogger<CacheService> logger = new TMBLogger<>(CacheService.class);
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public CacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void set(String key, String value){
        logger.info("Start Set key: {} , value: {} to Redis", key,value);
        redisTemplate.opsForValue().set(key, value);
        logger.info("Success Set key: {} , value: {} to Redis", key,value);
    }

    public void set(String key, String value, Long ttl){
        logger.info("Start Set key: {} , value: {} to Redis", key,value);
        redisTemplate.opsForValue().set(key, value, ttl);
        logger.info("Success Set key: {} , value: {} to Redis", key,value);
    }

    public String get(String key){
        return redisTemplate.opsForValue().get(key);
    }
}
