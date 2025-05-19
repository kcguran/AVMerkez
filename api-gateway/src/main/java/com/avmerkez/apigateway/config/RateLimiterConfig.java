package com.avmerkez.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            return Mono.just(clientIp);
        };
    }
} 