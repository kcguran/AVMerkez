package com.avmerkez.storeservice.controller;

import com.avmerkez.storeservice.dto.CreateStoreRequest;
import com.avmerkez.storeservice.dto.StoreDetailDto;
import com.avmerkez.storeservice.dto.StoreDto;
import com.avmerkez.storeservice.dto.UpdateStoreRequest;
import com.avmerkez.storeservice.entity.Brand;
import com.avmerkez.storeservice.entity.Category;
import com.avmerkez.storeservice.exception.GlobalExceptionHandler;
import com.avmerkez.storeservice.exception.ResourceNotFoundException;
import com.avmerkez.storeservice.service.StoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StoreController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class StoreControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @Autowired
    private ObjectMapper objectMapper;

    private StoreDto storeDto;
    private StoreDetailDto storeDetailDto;
    private CreateStoreRequest createStoreRequest;
    private UpdateStoreRequest updateStoreRequest;
    private final Long MALL_ID = 1L;
    private final Long STORE_ID = 1L;
    private final Long CATEGORY_ID = 1L;
    private final String CATEGORY_NAME = "Electronics";
    private final Long BRAND_ID = 1L;
    private final String BRAND_NAME = "Test Brand";
    private final Long NON_EXISTING_ID = 99L;

    @BeforeEach
    void setUp() {
        // Initialize the DTO objects with all required fields
        storeDto = new StoreDto(
                STORE_ID,
                "Test Store",
                MALL_ID,
                CATEGORY_ID,
                CATEGORY_NAME,
                BRAND_ID,
                BRAND_NAME,
                "1st Floor",
                "A-101",
                "Contact info",
                "Description",
                "logo.png"
        );
        
        // Set up StoreDetailDto with nested Category and Brand objects
        Category category = new Category(CATEGORY_NAME);
        category.setId(CATEGORY_ID);
        
        Brand brand = new Brand(BRAND_NAME);
        brand.setId(BRAND_ID);
        
        storeDetailDto = new StoreDetailDto(
                STORE_ID,
                "Test Store",
                MALL_ID,
                "1st Floor",
                "A-101",
                null, // Will be replaced with CategoryDto
                null, // Will be replaced with BrandDto
                "Contact info",
                "Description",
                "logo.png"
        );
        
        createStoreRequest = new CreateStoreRequest(
                MALL_ID,
                "Test Store", 
                CATEGORY_ID,
                "1st Floor", 
                "A-101",
                BRAND_ID,
                "Contact info",
                "Description",
                "logo.png"
        );
        
        updateStoreRequest = new UpdateStoreRequest(
                "Updated Store", 
                MALL_ID,
                CATEGORY_ID,
                BRAND_ID,
                "Ground Floor", 
                "G-05",
                "Updated contact",
                "Updated description",
                "updated-logo.png"
        );
    }

    @Test
    @DisplayName("Create store - Valid Request")
    void createStore_ShouldReturnCreated() throws Exception {
        given(storeService.createStore(any(CreateStoreRequest.class))).willReturn(storeDetailDto);

        mockMvc.perform(post("/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStoreRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(storeDetailDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(storeDetailDto.getName())));
    }

    @Test
    @DisplayName("Create store - Invalid Request (Blank Name)")
    void createStore_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        CreateStoreRequest invalidRequest = new CreateStoreRequest(
                MALL_ID, 
                "", // Invalid blank name
                CATEGORY_ID,
                "1st Floor", 
                "A-101",
                BRAND_ID,
                "Contact info",
                "Description",
                "logo.png"
        );

        mockMvc.perform(post("/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @DisplayName("Get store by ID - Found")
    void getStoreById_ShouldReturnStore() throws Exception {
        given(storeService.getStoreById(STORE_ID)).willReturn(storeDetailDto);

        mockMvc.perform(get("/stores/{id}", STORE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(STORE_ID.intValue())));
    }

    @Test
    @DisplayName("Get store by ID - Not Found")
    void getStoreById_ShouldReturnNotFound() throws Exception {
        given(storeService.getStoreById(NON_EXISTING_ID)).willThrow(new ResourceNotFoundException("Store", "id", NON_EXISTING_ID));

        mockMvc.perform(get("/stores/{id}", NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get stores by Mall ID - Found")
    void getStoresByMallId_ShouldReturnStoreList() throws Exception {
        List<StoreDto> storeList = Collections.singletonList(storeDto);
        given(storeService.getStoresByMallId(MALL_ID)).willReturn(storeList);

        mockMvc.perform(get("/stores").param("mallId", MALL_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].mallId", is(MALL_ID.intValue())));
    }

    @Test
    @DisplayName("Get stores by Mall ID - Not Found")
    void getStoresByMallId_ShouldReturnEmptyList() throws Exception {
        given(storeService.getStoresByMallId(NON_EXISTING_ID)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/stores").param("mallId", NON_EXISTING_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Update store - Valid Request")
    void updateStore_ShouldReturnOk() throws Exception {
        given(storeService.updateStore(eq(STORE_ID), any(UpdateStoreRequest.class))).willReturn(storeDetailDto);

        mockMvc.perform(put("/stores/{id}", STORE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStoreRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(storeDetailDto.getName())));
    }

    @Test
    @DisplayName("Update store - Not Found")
    void updateStore_ShouldReturnNotFound() throws Exception {
        given(storeService.updateStore(eq(NON_EXISTING_ID), any(UpdateStoreRequest.class))).willThrow(new ResourceNotFoundException("Store", "id", NON_EXISTING_ID));

        mockMvc.perform(put("/stores/{id}", NON_EXISTING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStoreRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete store - Found")
    void deleteStore_ShouldReturnNoContent() throws Exception {
        willDoNothing().given(storeService).deleteStore(STORE_ID);

        mockMvc.perform(delete("/stores/{id}", STORE_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete store - Not Found")
    void deleteStore_ShouldReturnNotFound() throws Exception {
        willThrow(new ResourceNotFoundException("Store", "id", NON_EXISTING_ID)).given(storeService).deleteStore(NON_EXISTING_ID);

        mockMvc.perform(delete("/stores/{id}", NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }
} 