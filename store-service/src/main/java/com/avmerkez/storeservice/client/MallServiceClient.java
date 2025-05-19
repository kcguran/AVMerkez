package com.avmerkez.storeservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Feign client interface for communicating with mall-service.
 */
@FeignClient(name = "mall-service", path = "/api/v1/malls", fallback = MallServiceClient.Fallback.class)
public interface MallServiceClient {

    /**
     * Checks if a mall exists by its ID.
     * Returns 2xx status if found, otherwise throws FeignException (e.g., 404).
     *
     * @param mallId The ID of the mall to check.
     * @return ResponseEntity with status code (body is ignored).
     */
    @RequestMapping(method = RequestMethod.HEAD, value = "/{mallId}/exists")
    ResponseEntity<Void> checkMallExists(@PathVariable("mallId") Long mallId);

    class Fallback implements MallServiceClient {
        @Override
        public ResponseEntity<Void> checkMallExists(Long mallId) {
            throw new IllegalStateException("Mall service is unavailable (fallback)");
        }
    }

    // Alternatively, return MallDto if needed later:
    // @GetMapping("/{mallId}")
    // ResponseEntity<MallDto> getMallById(@PathVariable("mallId") Long mallId);
} 