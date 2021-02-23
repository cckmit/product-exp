package com.tmb.oneapp.productsexpservice.util;

import com.tmb.common.logger.TMBLogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.redis.core.RedisTemplate;



import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CacheServiceTest {
    @Mock
    TMBLogger<CacheService> logger;
    @Mock
    RedisTemplate<String, String> redisTemplate;
    @InjectMocks
    CacheService cacheService;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        redisTemplate.opsForValue().set("1","22");
        cacheService.set("1","11");
    }

    @Test
    public void testSet() throws Exception {
        cacheService.set("1", "22");

        assertEquals(1,redisTemplate.opsForValue().get("1"));

    }

    @Test
    public void testSet2() throws Exception {
        cacheService.set("1", "1", Long.valueOf(1));
        assertEquals(1,redisTemplate.opsForValue());

    }

    @Test
    public void testGet() throws Exception {
        String result = cacheService.get("key");
        assertEquals("replaceMeWithExpectedResult", result);
    }
}