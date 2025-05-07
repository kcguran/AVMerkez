package com.avmerkez.userservice.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) 
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.bootstrap.enabled=false",
    "spring.config.import=",
    "eureka.client.enabled=false"
})
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String PROTECTED_ENDPOINT = "/api/v1/users/me";
    private static final String ACTUATOR_HEALTH = "/actuator/health";

    @Test
    @DisplayName("Accessing protected endpoint without authentication should return 401 Unauthorized")
    void accessProtectedEndpoint_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get(PROTECTED_ENDPOINT))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Accessing actuator health endpoint without authentication should be permitted if configured")
    @WithMockUser
    void accessActuatorHealth_ReturnsOk() throws Exception {
        mockMvc.perform(get(ACTUATOR_HEALTH))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Accessing root path without authentication should return 401 Unauthorized")
    void accessRootPath_Unauthenticated_ReturnsUnauthorized() throws Exception {
         mockMvc.perform(get("/"))
                .andExpect(status().isUnauthorized());
    }
} 