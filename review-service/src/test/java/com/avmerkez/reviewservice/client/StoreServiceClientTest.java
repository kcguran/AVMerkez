package com.avmerkez.reviewservice.client;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StoreServiceClientTest {
    @Test
    void fallback_checkStoreExists_throwsException() {
        StoreServiceClient.Fallback fallback = new StoreServiceClient.Fallback();
        assertThatThrownBy(() -> fallback.checkStoreExists(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Store service is unavailable");
    }
} 