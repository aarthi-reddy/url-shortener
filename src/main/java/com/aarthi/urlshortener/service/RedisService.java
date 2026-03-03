package com.aarthi.urlshortener.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long CACHE_TTL_HOURS = 24;

    public void cacheUrl(String shortCode, String originalUrl) {
        redisTemplate.opsForValue().set(
                shortCode,
                originalUrl,
                CACHE_TTL_HOURS,
                TimeUnit.HOURS
        );
    }

    public String getCachedUrl(String shortCode) {
        return redisTemplate.opsForValue().get(shortCode);
    }

    public void deleteCache(String shortCode) {
        redisTemplate.delete(shortCode);
    }
}