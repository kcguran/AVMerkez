package com.avmerkez.storeservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client interface for communicating with mall-service.
 */
@FeignClient(name = "mall-service", path = "/api/v1/malls") // service-id from Eureka and base path
public interface MallServiceClient {

    /**
     * Checks if a mall exists by its ID.
     * Returns 2xx status if found, otherwise throws FeignException (e.g., 404).
     *
     * @param mallId The ID of the mall to check.
     * @return ResponseEntity with status code (body is ignored).
     */
    @GetMapping("/{mallId}")
    ResponseEntity<Void> checkMallExists(@PathVariable("mallId") Long mallId);

    // Alternatively, return MallDto if needed later:
    // @GetMapping("/{mallId}")
    // ResponseEntity<MallDto> getMallById(@PathVariable("mallId") Long mallId);
} 