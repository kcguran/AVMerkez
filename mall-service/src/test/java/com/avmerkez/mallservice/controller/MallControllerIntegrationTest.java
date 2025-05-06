package com.avmerkez.mallservice.controller;

import com.avmerkez.mallservice.dto.CreateMallRequest;
import com.avmerkez.mallservice.dto.MallDto;
import com.avmerkez.mallservice.dto.UpdateMallRequest;
import com.avmerkez.mallservice.exception.ResourceNotFoundException;
import com.avmerkez.mallservice.service.MallService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


// @WebMvcTest yerine @SpringBootTest ve @AutoConfigureMockMvc kullan
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
                properties = {
                    "spring.security.enabled=false", // Disable security for tests
                    "spring.cloud.config.fail-fast=false", // Explicitly disable fail-fast for config client in tests
                    "spring.cloud.bootstrap.enabled=false", // Disable bootstrap context for tests
                    "spring.config.import=" // Override/disable config server import for tests
                })
@ActiveProfiles("test") // Test profilini aktif et
@AutoConfigureMockMvc // MockMvc'yi otomatik yapılandır
@Testcontainers // Enable Testcontainers support
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Bunu şimdilik tutalım
class MallControllerIntegrationTest {

    // Define the PostgreSQL container
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    // Dynamically set datasource properties
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        // Ensure Flyway uses the test container datasource
        registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.flyway.user", postgreSQLContainer::getUsername);
        registry.add("spring.flyway.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc; // AutoConfigureMockMvc ile inject edilir

    @MockBean // @SpringBootTest ile de @MockBean kullanılabilir
    private MallService mallService;

    @Autowired
    private ObjectMapper objectMapper;

    private MallDto mallDto;
    private CreateMallRequest createMallRequest;
    private UpdateMallRequest updateMallRequest;

    @BeforeEach
    void setUp() {
        mallDto = new MallDto(1L, "Test Mall", "Test City", "Test District");
        createMallRequest = new CreateMallRequest("Test Mall", "Test Address", "Test City", "Test District");
        updateMallRequest = new UpdateMallRequest("Updated Mall", "Updated Address", "Updated City", "Updated District");
    }

    @Test
    void createMall_ShouldReturnCreatedMall_WhenRequestIsValid() throws Exception {
        // Given (BDD - Behavior Driven Development tarzı)
        given(mallService.createMall(any(CreateMallRequest.class))).willReturn(mallDto);

        // When & Then
        mockMvc.perform(post("/malls") // POST isteği /malls endpoint'ine
                        .contentType(MediaType.APPLICATION_JSON) // İstek tipi JSON
                        .content(objectMapper.writeValueAsString(createMallRequest))) // İstek body'si
                .andExpect(status().isCreated()) // Yanıt durum kodunun 201 (Created) olmasını bekle
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1))) // Yanıt JSON'ında id alanının 1 olmasını bekle
                .andExpect(jsonPath("$.name", is(mallDto.getName()))); // name alanının eşleşmesini bekle
    }

    @Test
    void createMall_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        // Given
        CreateMallRequest invalidRequest = new CreateMallRequest("", "Address", "City", "District");

        // When & Then
        mockMvc.perform(post("/malls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()) // Yanıt durum kodunun 400 (Bad Request) olmasını bekle
                // GlobalExceptionHandler'dan dönen hata yapısını kontrol et
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errors.name").exists()) // 'name' alanı için hata olmalı
                .andExpect(jsonPath("$.errors.name", hasSize(2))); // @NotBlank ve @Size için 2 hata
    }

    @Test
    void getMallById_ShouldReturnMallDto_WhenIdExists() throws Exception {
        // Given
        given(mallService.getMallById(1L)).willReturn(mallDto);

        // When & Then
        mockMvc.perform(get("/malls/{id}", 1L)) // GET isteği /malls/1 endpoint'ine
                .andExpect(status().isOk()) // Yanıt durum kodunun 200 (OK) olmasını bekle
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(mallDto.getName())));
    }

    @Test
    void getMallById_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        // Given
        given(mallService.getMallById(anyLong())).willThrow(new ResourceNotFoundException("Mall", "id", 99L));

        // When & Then
        mockMvc.perform(get("/malls/{id}", 99L))
                .andExpect(status().isNotFound()); // Yanıt durum kodunun 404 (Not Found) olmasını bekle
    }

    @Test
    void getAllMalls_ShouldReturnListOfMalls() throws Exception {
        // Given
        given(mallService.getAllMalls(any(), any())).willReturn(Collections.singletonList(mallDto));

        // When & Then
        mockMvc.perform(get("/malls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Dönen listenin boyutunun 1 olmasını bekle
                .andExpect(jsonPath("$[0].id", is(1))); // Listenin ilk elemanının id'sinin 1 olmasını bekle
    }

    @Test
    void updateMall_ShouldReturnUpdatedMall_WhenRequestIsValid() throws Exception {
        // Given
        MallDto updatedDto = new MallDto(1L, updateMallRequest.getName(), updateMallRequest.getCity(), updateMallRequest.getDistrict());
        // Servis mock'unu güncellenmiş DTO'yu döndürecek şekilde ayarla
        given(mallService.updateMall(anyLong(), any(UpdateMallRequest.class))).willReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/malls/{id}", 1L) // PUT isteği /malls/1 endpoint'ine
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMallRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                // Güncellenmiş alanları kontrol et
                .andExpect(jsonPath("$.name", is(updatedDto.getName())))
                .andExpect(jsonPath("$.city", is(updatedDto.getCity())))
                .andExpect(jsonPath("$.district", is(updatedDto.getDistrict())));
    }

    @Test
    void updateMall_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        // Given
        given(mallService.updateMall(anyLong(), any(UpdateMallRequest.class)))
                .willThrow(new ResourceNotFoundException("Mall", "id", 99L));

        // When & Then
        mockMvc.perform(put("/malls/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMallRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMall_ShouldReturnNoContent_WhenIdExists() throws Exception {
        // Given
        // doNothing().when(mallService).deleteMall(anyLong()); // deleteMall void olduğu için doNothing
        willDoNothing().given(mallService).deleteMall(anyLong()); // BDDMockito style

        // When & Then
        mockMvc.perform(delete("/malls/{id}", 1L)) // DELETE isteği /malls/1 endpoint'ine
                .andExpect(status().isNoContent()); // Yanıt durum kodunun 204 (No Content) olmasını bekle
    }

    @Test
    void deleteMall_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        // Given
        // doThrow(new ResourceNotFoundException("Mall", "id", 99L)).when(mallService).deleteMall(anyLong());
        willThrow(new ResourceNotFoundException("Mall", "id", 99L)).given(mallService).deleteMall(anyLong()); // BDDMockito style

        // When & Then
        mockMvc.perform(delete("/malls/{id}", 99L))
                .andExpect(status().isNotFound());
    }
} 