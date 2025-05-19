package com.avmerkez.reviewservice.client;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MallServiceClientTest {
    @Test
    void fallback_checkMallExists_throwsException() {
        MallServiceClient.Fallback fallback = new MallServiceClient.Fallback();
        assertThatThrownBy(() -> fallback.checkMallExists(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Mall service is unavailable");
    }
} 