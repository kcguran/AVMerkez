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
import java.util.List;

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

    // Define the PostgreSQL container with PostGIS support
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgis/postgis:15-3.4")
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
        mallDto = new MallDto(1L, "Test Mall", "Test City", "Test District", "Test Address", 39.92, 32.85, "10-22", "web.com", "123");
        createMallRequest = new CreateMallRequest("Test Mall", "Test Address", "Test City", "Test District", 39.92, 32.85, "10-22", "web.com", "123");
        updateMallRequest = new UpdateMallRequest("Updated Mall", "Updated Address", "Updated City", "Updated District", 39.93, 32.86, "09-23", "newweb.com", "456");
    }

    @Test
    void createMall_ShouldReturnCreatedMall_WhenRequestIsValid() throws Exception {
        // Given (BDD - Behavior Driven Development tarzı)
        given(mallService.createMall(any(CreateMallRequest.class))).willReturn(mallDto);

        // When & Then
        mockMvc.perform(post("/api/v1/malls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMallRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Mall created successfully.")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is(mallDto.getName())));
    }

    @Test
    void createMall_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        // Given
        CreateMallRequest invalidRequest = new CreateMallRequest(
                "", // name (blank)
                "Address", // address
                "City", // city
                "District", // district
                null, // latitude
                null, // longitude
                null, // workingHours
                null, // website
                null  // phoneNumber
        );

        // When & Then
        // Bu endpoint doğrudan GlobalExceptionHandler tarafından ele alınacağı için
        // yanıt yapısı GenericApiResponse olmayacak, ErrorResponse olacak.
        // Bu test olduğu gibi kalmalı.
        mockMvc.perform(post("/api/v1/malls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));
                // .andExpect(jsonPath("$.message", is("Validation failed")))
                // .andExpect(jsonPath("$.errors.name").exists())
                // .andExpect(jsonPath("$.errors.name", hasSize(2)));
    }

    @Test
    void getMallById_ShouldReturnMallDto_WhenIdExists() throws Exception {
        // Given
        given(mallService.getMallById(1L)).willReturn(mallDto);

        // When & Then
        mockMvc.perform(get("/api/v1/malls/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is(mallDto.getName())));
    }

    @Test
    void getMallById_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        // Given
        given(mallService.getMallById(anyLong())).willThrow(new ResourceNotFoundException("Mall", "id", 99L));

        // When & Then
        // GlobalExceptionHandler ErrorResponse dönecek, GenericApiResponse değil.
        mockMvc.perform(get("/api/v1/malls/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    void getAllMalls_ShouldReturnListOfMalls() throws Exception {
        // Given
        given(mallService.getAllMalls(any(), any())).willReturn(Collections.singletonList(mallDto));

        // When & Then
        mockMvc.perform(get("/api/v1/malls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(1)));
    }

    @Test
    void updateMall_ShouldReturnUpdatedMall_WhenRequestIsValid() throws Exception {
        // Given
        MallDto updatedDto = new MallDto(
                1L, // id
                updateMallRequest.getName(), // name
                updateMallRequest.getCity(), // city
                updateMallRequest.getDistrict(), // district
                updateMallRequest.getAddress(), // address
                updateMallRequest.getLatitude(), // latitude
                updateMallRequest.getLongitude(), // longitude
                updateMallRequest.getWorkingHours(), // workingHours
                updateMallRequest.getWebsite(), // website
                updateMallRequest.getPhoneNumber() // phoneNumber
        );
        given(mallService.updateMall(anyLong(), any(UpdateMallRequest.class))).willReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/malls/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMallRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Mall updated successfully.")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is(updatedDto.getName())))
                .andExpect(jsonPath("$.data.city", is(updatedDto.getCity())))
                .andExpect(jsonPath("$.data.district", is(updatedDto.getDistrict())));
    }

    @Test
    void updateMall_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        // Given
        given(mallService.updateMall(anyLong(), any(UpdateMallRequest.class)))
                .willThrow(new ResourceNotFoundException("Mall", "id", 99L));

        // When & Then
        mockMvc.perform(put("/api/v1/malls/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMallRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void deleteMall_ShouldReturnNoContent_WhenIdExists() throws Exception {
        // Bu test olduğu gibi kalır, çünkü 204 No Content yanıtı body içermez.
        willDoNothing().given(mallService).deleteMall(anyLong());

        mockMvc.perform(delete("/api/v1/malls/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMall_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        // GlobalExceptionHandler ErrorResponse dönecek.
        willThrow(new ResourceNotFoundException("Mall", "id", 99L)).given(mallService).deleteMall(anyLong());

        mockMvc.perform(delete("/api/v1/malls/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void findMallsNearLocation_ShouldReturnListOfMalls_WhenParamsAreValid() throws Exception {
        // Given
        double lat = 39.920770;
        double lon = 32.854110;
        double dist = 5.0;
        List<MallDto> expectedMalls = Collections.singletonList(mallDto);

        given(mallService.findMallsNearLocation(lat, lon, dist)).willReturn(expectedMalls);

        // When & Then
        mockMvc.perform(get("/api/v1/malls/near")
                        .param("latitude", String.valueOf(lat))
                        .param("longitude", String.valueOf(lon))
                        .param("distance", String.valueOf(dist)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(mallDto.getId().intValue())))
                .andExpect(jsonPath("$.data[0].name", is(mallDto.getName())));
    }

    @Test
    void findMallsNearLocation_ShouldReturnBadRequest_WhenLatitudeIsInvalid() throws Exception {
        // GlobalExceptionHandler ErrorResponse dönecek.
        double invalidLat = -91.0;
        double lon = 32.854110;
        double dist = 5.0;

        mockMvc.perform(get("/api/v1/malls/near")
                        .param("latitude", String.valueOf(invalidLat))
                        .param("longitude", String.valueOf(lon))
                        .param("distance", String.valueOf(dist)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void findMallsNearLocation_ShouldReturnBadRequest_WhenDistanceIsNegative() throws Exception {
        // GlobalExceptionHandler ErrorResponse dönecek.
        double lat = 39.920770;
        double lon = 32.854110;
        double invalidDist = -5.0;

        mockMvc.perform(get("/api/v1/malls/near")
                        .param("latitude", String.valueOf(lat))
                        .param("longitude", String.valueOf(lon))
                        .param("distance", String.valueOf(invalidDist)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void findMallsNearLocation_ShouldReturnEmptyList_WhenServiceReturnsEmpty() throws Exception {
        // Given
        double lat = 39.920770;
        double lon = 32.854110;
        double dist = 5.0;

        given(mallService.findMallsNearLocation(lat, lon, dist)).willReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/v1/malls/near")
                        .param("latitude", String.valueOf(lat))
                        .param("longitude", String.valueOf(lon))
                        .param("distance", String.valueOf(dist)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }
} 