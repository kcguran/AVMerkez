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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private static final String AUTH_REGISTER = "/api/v1/auth/register";
    private static final String AUTH_LOGIN = "/api/v1/auth/login";

    @Test
    @DisplayName("Accessing protected endpoint without authentication should return 401 Unauthorized")
    void accessProtectedEndpoint_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get(PROTECTED_ENDPOINT))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Accessing actuator health endpoint without authentication should be permitted")
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
    
    @Test
    @DisplayName("Auth register endpoint should be publicly accessible")
    void accessAuthRegister_ReturnsPermitted() throws Exception {
        mockMvc.perform(post(AUTH_REGISTER))
                .andExpect(status().is4xxClientError()); // 400 expected due to missing request body, not 401 unauthorized
    }
    
    @Test
    @DisplayName("Auth login endpoint should be publicly accessible")
    void accessAuthLogin_ReturnsPermitted() throws Exception {
        mockMvc.perform(post(AUTH_LOGIN))
                .andExpect(status().is4xxClientError()); // 400 expected due to missing request body, not 401 unauthorized
    }
    
    @Test
    @DisplayName("Protected endpoint with authenticated user should return OK")
    @WithMockUser
    void accessProtectedEndpoint_Authenticated_ReturnsOk() throws Exception {
        mockMvc.perform(get(PROTECTED_ENDPOINT))
                .andExpect(status().isOk());
    }
} 