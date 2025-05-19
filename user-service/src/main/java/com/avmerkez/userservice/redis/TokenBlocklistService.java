package com.avmerkez.userservice.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenBlocklistService {
    private static final String BLOCKLIST_PREFIX = "jwt:blocklist:";

    private final StringRedisTemplate redisTemplate;

    public TokenBlocklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blockToken(String jti, long expirationMillis) {
        redisTemplate.opsForValue().set(BLOCKLIST_PREFIX + jti, "1", Duration.ofMillis(expirationMillis));
    }

    public boolean isTokenBlocked(String jti) {
        return redisTemplate.hasKey(BLOCKLIST_PREFIX + jti);
    }
} 