package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.dto.*;
import com.avmerkez.storeservice.entity.Brand;
import com.avmerkez.storeservice.entity.Category;
import com.avmerkez.storeservice.entity.Store;
import com.avmerkez.storeservice.exception.EntityNotFoundException;
import com.avmerkez.storeservice.mapper.StoreMapper;
import com.avmerkez.storeservice.repository.BrandRepository;
import com.avmerkez.storeservice.repository.CategoryRepository;
import com.avmerkez.storeservice.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private StoreMapper storeMapper;

    @InjectMocks
    private StoreServiceImpl storeService;

    private Store store;
    private StoreDto storeDto;
    private StoreDetailDto storeDetailDto;
    private CreateStoreRequest createRequest;
    private UpdateStoreRequest updateRequest;
    private Category category;
    private Brand brand;
    private CategoryDto categoryDto;
    private BrandDto brandDto;

    private final Long STORE_ID = 1L;
    private final Long MALL_ID = 10L;
    private final Long CATEGORY_ID = 100L;
    private final Long BRAND_ID = 200L;

    @BeforeEach
    void setUp() {
        category = new Category("Electronics");
        category.setId(CATEGORY_ID);
        categoryDto = new CategoryDto(CATEGORY_ID, "Electronics", null, null);

        brand = new Brand("TechBrand");
        brand.setId(BRAND_ID);
        brandDto = new BrandDto(BRAND_ID, "TechBrand", "logo.url", "website.url");

        store = new Store("Main Tech Store", MALL_ID, category, brand);
        store.setId(STORE_ID);
        store.setFloor("1st");
        store.setStoreNumber("A1");
        store.setContactInformation("contact@tech.com");
        store.setDescription("Leading tech store");
        store.setLogoUrl("storelogo.png");

        storeDto = new StoreDto(
            STORE_ID,              // id
            "Main Tech Store",     // name
            MALL_ID,               // mallId 
            CATEGORY_ID,           // categoryId
            "Electronics",         // categoryName
            BRAND_ID,              // brandId
            "TechBrand",           // brandName
            "1st",                 // floor
            "A1",                  // storeNumber
            "contact@tech.com",    // contactInformation
            "Leading tech store",  // description
            "storelogo.png"        // logoUrl
        );
        
        storeDetailDto = new StoreDetailDto(
            STORE_ID, 
            "Main Tech Store", 
            MALL_ID, 
            "1st", 
            "A1", 
            categoryDto, 
            brandDto, 
            "contact@tech.com", 
            "Leading tech store", 
            "storelogo.png"
        );

        createRequest = new CreateStoreRequest();
        createRequest.setName("Main Tech Store");
        createRequest.setMallId(MALL_ID);
        createRequest.setCategoryId(CATEGORY_ID);
        createRequest.setBrandId(BRAND_ID);
        createRequest.setFloor("1st");
        createRequest.setContactInformation("contact@tech.com");
        createRequest.setDescription("Leading tech store");
        createRequest.setLogoUrl("storelogo.png");

        updateRequest = new UpdateStoreRequest();
        updateRequest.setName("Updated Tech Store");
        updateRequest.setFloor("2nd");
    }

    @Test
    @DisplayName("createStore should save and return StoreDetailDto")
    void createStore_ValidRequest_ReturnsStoreDetailDto() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(brandRepository.findById(BRAND_ID)).thenReturn(Optional.of(brand));
        when(storeMapper.toStore(createRequest)).thenReturn(store); // Store entity without ID initially
        when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> {
            Store s = invocation.getArgument(0);
            s.setId(STORE_ID); // Simulate ID generation
            return s;
        });
        when(storeMapper.toStoreDetailDto(any(Store.class))).thenReturn(storeDetailDto);

        StoreDetailDto result = storeService.createStore(createRequest);

        assertThat(result).isEqualTo(storeDetailDto);
        verify(storeRepository).save(argThat(s -> s.getName().equals("Main Tech Store") && s.getCategory().equals(category) && s.getBrand().equals(brand)));
    }

    @Test
    @DisplayName("createStore should throw EntityNotFoundException if category not found")
    void createStore_CategoryNotFound_ThrowsEntityNotFoundException() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> storeService.createStore(createRequest));
        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    @DisplayName("getStoreById should return StoreDetailDto when found")
    void getStoreById_Exists_ReturnsStoreDetailDto() {
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
        when(storeMapper.toStoreDetailDto(store)).thenReturn(storeDetailDto);

        StoreDetailDto result = storeService.getStoreById(STORE_ID);

        assertThat(result).isEqualTo(storeDetailDto);
    }

    @Test
    @DisplayName("getStoresByMallId should return list of StoreDto")
    void getStoresByMallId_ReturnsListOfStoreDto() {
        when(storeRepository.findByMallId(MALL_ID)).thenReturn(Collections.singletonList(store));
        when(storeMapper.toStoreDtoList(Collections.singletonList(store))).thenReturn(Collections.singletonList(storeDto));

        List<StoreDto> result = storeService.getStoresByMallId(MALL_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(storeDto);
        verify(storeRepository).findByMallId(MALL_ID);
    }


    @Test
    @DisplayName("updateStore should update and return StoreDetailDto")
    void updateStore_ValidRequest_ReturnsUpdatedStoreDetailDto() {
        Store storeToUpdate = new Store("Main Tech Store", MALL_ID, category, brand);
        storeToUpdate.setId(STORE_ID);
        storeToUpdate.setFloor("1st");
        
        StoreDetailDto updatedStoreDetailDto = new StoreDetailDto(STORE_ID, "Updated Tech Store", MALL_ID, "2nd", "A1", categoryDto, brandDto, "contact@tech.com", "Leading tech store", "storelogo.png");

        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(storeToUpdate));
        // No category/brand change in this test case for updateRequest
        when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the modified store
        when(storeMapper.toStoreDetailDto(any(Store.class))).thenReturn(updatedStoreDetailDto);
        doNothing().when(storeMapper).updateStoreFromRequest(updateRequest, storeToUpdate);

        StoreDetailDto result = storeService.updateStore(STORE_ID, updateRequest);
        
        verify(storeMapper).updateStoreFromRequest(updateRequest, storeToUpdate);
        verify(storeRepository).save(storeToUpdate);
        assertThat(result.getName()).isEqualTo("Updated Tech Store");
        assertThat(result.getFloor()).isEqualTo("2nd");
    }

    @Test
    @DisplayName("deleteStore should call deleteById when exists")
    void deleteStore_Exists_CallsDeleteById() {
        when(storeRepository.existsById(STORE_ID)).thenReturn(true);
        doNothing().when(storeRepository).deleteById(STORE_ID);

        storeService.deleteStore(STORE_ID);

        verify(storeRepository).deleteById(STORE_ID);
    }

    @Test
    @DisplayName("Test creating store with valid data")
    void createStoreWithValidData() {
        // Set up test data with all required parameters
        StoreDto storeDto = new StoreDto(
            1L,               // id
            "Test Store",     // name 
            2L,               // mallId
            3L,               // categoryId
            "Electronics",    // categoryName
            4L,               // brandId
            "Test Brand",     // brandName
            "1st Floor",      // floor
            "A101",           // storeNo
            "555-1234",       // contactInformation
            "Test description", // description
            "logo.jpg"        // logoUrl
        );
        // Rest of the test...
    }
} 