package com.avmerkez.searchservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Set;

@FeignClient(name = "user-service", path = "/api/v1/users", fallback = UserServiceClient.Fallback.class)
public interface UserServiceClient {
    @GetMapping("/{userId}/favorites/malls")
    Set<Long> getFavoriteMallIds(@PathVariable("userId") Long userId);

    class Fallback implements UserServiceClient {
        @Override
        public Set<Long> getFavoriteMallIds(Long userId) {
            return Set.of();
        }
    }
} 