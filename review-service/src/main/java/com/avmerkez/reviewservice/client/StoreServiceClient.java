package com.avmerkez.reviewservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Feign client for communicating with store-service.
 */
@FeignClient(name = "store-service", path = "/api/v1/stores", fallback = StoreServiceClient.Fallback.class)
public interface StoreServiceClient {

    /**
     * Checks if a store exists by its ID.
     * Should have an endpoint like /api/v1/stores/{storeId}/exists in store-service.
     * Returns 2xx status if found, otherwise throws FeignException (e.g., 404).
     *
     * @param storeId The ID of the store to check.
     * @return ResponseEntity with status code (body is ignored).
     */
    @RequestMapping(method = RequestMethod.HEAD, value = "/{storeId}/exists")
    ResponseEntity<Void> checkStoreExists(@PathVariable("storeId") Long storeId);

    class Fallback implements StoreServiceClient {
        @Override
        public ResponseEntity<Void> checkStoreExists(Long storeId) {
            throw new IllegalStateException("Store service is unavailable (fallback)");
        }
    }
} 