package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.dto.CreateStoreRequest;
import com.avmerkez.storeservice.dto.StoreDetailDto;
import com.avmerkez.storeservice.dto.StoreDto;
import com.avmerkez.storeservice.dto.UpdateStoreRequest;
import com.avmerkez.storeservice.entity.Brand;
import com.avmerkez.storeservice.entity.Category;
import com.avmerkez.storeservice.entity.Store;
import com.avmerkez.storeservice.exception.ResourceNotFoundException;
import com.avmerkez.storeservice.mapper.StoreMapper;
import com.avmerkez.storeservice.repository.StoreRepository;
import com.avmerkez.storeservice.client.MallServiceClient;
import com.avmerkez.storeservice.exception.InvalidInputException;
import com.avmerkez.storeservice.repository.BrandRepository;
import com.avmerkez.storeservice.repository.CategoryRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Initialize mocks
class StoreServiceImplTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreMapper storeMapper;

    @Mock
    private MallServiceClient mallServiceClient;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private BrandRepository brandRepository;

    @InjectMocks // Inject mocks into this instance
    private StoreServiceImpl storeService;

    private Store store;
    private StoreDto storeDto;
    private StoreDetailDto storeDetailDto;
    private CreateStoreRequest createStoreRequest;
    private UpdateStoreRequest updateStoreRequest;
    private Category category;
    private Brand brand;

    @BeforeEach
    void setUp() {
        // Initialize Category and Brand
        category = new Category("Electronics");
        category.setId(1L);
        
        brand = new Brand("Test Brand");
        brand.setId(1L);
        
        // Create Store using constructor and setters instead of builder
        store = new Store();
        store.setId(1L);
        store.setMallId(10L);
        store.setName("Test Store");
        store.setCategory(category);
        store.setBrand(brand);
        store.setFloor("Test Floor");
        store.setStoreNumber("T-1");
        store.setContactInformation("Contact Info");
        store.setDescription("Description");
        store.setLogoUrl("logo.png");

        // Initialize StoreDto with all parameters
        storeDto = new StoreDto(
            1L, 
            "Test Store",
            10L, 
            1L, 
            "Electronics",
            1L, 
            "Test Brand",
            "Test Floor", 
            "T-1",
            "Contact Info",
            "Description",
            "logo.png"
        );
        
        // Initialize StoreDetailDto
        storeDetailDto = new StoreDetailDto(
            1L, 
            "Test Store",
            10L, 
            "Test Floor", 
            "T-1",
            null, // This would be a CategoryDto in real scenario
            null, // This would be a BrandDto in real scenario
            "Contact Info",
            "Description",
            "logo.png"
        );

        // Initialize CreateStoreRequest with all required parameters
        createStoreRequest = new CreateStoreRequest(
            10L, 
            "New Store", 
            1L, 
            "New Floor", 
            "N-1",
            1L, 
            "Contact Info",
            "Description",
            "logo.png"
        );
        
        // Initialize UpdateStoreRequest with all parameters
        updateStoreRequest = new UpdateStoreRequest(
            "Updated Store", 
            10L, 
            1L, 
            1L,
            "Updated Floor", 
            "U-1",
            "Updated Contact Info",
            "Updated Description",
            "updated-logo.png"
        );
    }

    @Test
    @DisplayName("Create store successfully")
    void createStore_ShouldReturnStoreDto_WhenMallIdIsValid() {
        // Given
        Long validMallId = createStoreRequest.getMallId();
        given(mallServiceClient.checkMallExists(validMallId)).willReturn(ResponseEntity.ok().build());
        given(categoryRepository.findById(createStoreRequest.getCategoryId())).willReturn(Optional.of(category));
        given(brandRepository.findById(createStoreRequest.getBrandId())).willReturn(Optional.of(brand));
        given(storeMapper.toStore(createStoreRequest)).willReturn(store);
        given(storeRepository.save(any(Store.class))).willReturn(store);
        given(storeMapper.toStoreDetailDto(store)).willReturn(storeDetailDto);

        // When
        StoreDetailDto created = storeService.createStore(createStoreRequest);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo(storeDetailDto.getName());
        verify(mallServiceClient, times(1)).checkMallExists(validMallId);
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    @DisplayName("Create store should throw InvalidInputException when Mall ID is null")
    void createStore_ShouldThrowException_WhenMallIdIsNull() {
        // Given
        createStoreRequest.setMallId(null);

        // When & Then
        assertThrows(InvalidInputException.class, () -> {
            storeService.createStore(createStoreRequest);
        });

        verify(mallServiceClient, never()).checkMallExists(anyLong());
        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    @DisplayName("Create store should throw InvalidInputException when Mall ID does not exist")
    void createStore_ShouldThrowException_WhenMallIdNotFound() {
        // Given
        Long invalidMallId = 999L;
        createStoreRequest.setMallId(invalidMallId);
        // Mock Feign client call to throw NotFound exception
        Request dummyRequest = Request.create(Request.HttpMethod.GET, "/api/v1/malls/" + invalidMallId, Collections.emptyMap(), null, new RequestTemplate());
        given(mallServiceClient.checkMallExists(invalidMallId))
                .willThrow(new FeignException.NotFound("Mall not found", dummyRequest, null, null));

        // When & Then
        assertThrows(InvalidInputException.class, () -> {
            storeService.createStore(createStoreRequest);
        });

        verify(mallServiceClient, times(1)).checkMallExists(invalidMallId);
        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    @DisplayName("Create store should throw RuntimeException for other Feign errors")
    void createStore_ShouldThrowRuntimeException_WhenFeignErrorOccurs() {
        // Given
        Long mallId = createStoreRequest.getMallId();
        // Mock Feign client call to throw a generic FeignException
        Request dummyRequest = Request.create(Request.HttpMethod.GET, "/api/v1/malls/" + mallId, Collections.emptyMap(), null, new RequestTemplate());
        given(mallServiceClient.checkMallExists(mallId))
                .willThrow(new FeignException.InternalServerError("Server error", dummyRequest, null, null));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            storeService.createStore(createStoreRequest);
        });

        verify(mallServiceClient, times(1)).checkMallExists(mallId);
        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    @DisplayName("Get store by existing ID")
    void getStoreById_ShouldReturnStoreDto_WhenIdExists() {
        // Given
        given(storeRepository.findById(1L)).willReturn(Optional.of(store));
        given(storeMapper.toStoreDetailDto(store)).willReturn(storeDetailDto);

        // When
        StoreDetailDto found = storeService.getStoreById(1L);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Get store by non-existing ID")
    void getStoreById_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist() {
        // Given
        given(storeRepository.findById(99L)).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> storeService.getStoreById(99L));
        verify(storeMapper, never()).toStoreDetailDto(any());
    }

    @Test
    @DisplayName("Get stores by existing Mall ID")
    void getStoresByMallId_ShouldReturnStoreDtoList() {
        // Given
        given(storeRepository.findByMallId(10L)).willReturn(Collections.singletonList(store));
        given(storeMapper.toStoreDtoList(Collections.singletonList(store))).willReturn(Collections.singletonList(storeDto));

        // When
        List<StoreDto> stores = storeService.getStoresByMallId(10L);

        // Then
        assertThat(stores).isNotEmpty();
        assertThat(stores.get(0).getMallId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Get stores by non-existing Mall ID")
    void getStoresByMallId_ShouldReturnEmptyList() {
        // Given
        given(storeRepository.findByMallId(99L)).willReturn(Collections.emptyList());
        given(storeMapper.toStoreDtoList(Collections.emptyList())).willReturn(Collections.emptyList());

        // When
        List<StoreDto> stores = storeService.getStoresByMallId(99L);

        // Then
        assertThat(stores).isEmpty();
        verify(storeMapper, times(1)).toStoreDtoList(Collections.emptyList());
    }

    @Test
    @DisplayName("Get all stores")
    void getAllStores_ShouldReturnAllStores() {
        // Given
        given(storeRepository.findAll()).willReturn(Collections.singletonList(store));
        given(storeMapper.toStoreDtoList(Collections.singletonList(store))).willReturn(Collections.singletonList(storeDto));

        // When
        List<StoreDto> allStores = storeService.getAllStores();

        // Then
        assertThat(allStores).hasSize(1);
        verify(storeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Update store successfully")
    void updateStore_ShouldReturnUpdatedStoreDto() {
        // Given
        given(storeRepository.findById(1L)).willReturn(Optional.of(store));
        given(storeRepository.save(any(Store.class))).willReturn(store);
        given(storeMapper.toStoreDetailDto(store)).willReturn(storeDetailDto);
        willDoNothing().given(storeMapper).updateStoreFromRequest(any(UpdateStoreRequest.class), any(Store.class));

        // When
        StoreDetailDto updated = storeService.updateStore(1L, updateStoreRequest);

        // Then
        assertThat(updated).isNotNull();
        verify(storeMapper, times(1)).updateStoreFromRequest(eq(updateStoreRequest), eq(store));
        verify(storeRepository, times(1)).save(store);
    }

    @Test
    @DisplayName("Update non-existing store")
    void updateStore_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist() {
        // Given
        given(storeRepository.findById(99L)).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> storeService.updateStore(99L, updateStoreRequest));
        verify(storeRepository, never()).save(any());
        verify(storeMapper, never()).updateStoreFromRequest(any(), any());
    }

    @Test
    @DisplayName("Delete store successfully")
    void deleteStore_ShouldCompleteSuccessfully_WhenIdExists() {
        // Given
        given(storeRepository.existsById(1L)).willReturn(true);
        willDoNothing().given(storeRepository).deleteById(1L);

        // When
        storeService.deleteStore(1L);

        // Then
        verify(storeRepository, times(1)).existsById(1L);
        verify(storeRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Delete non-existing store")
    void deleteStore_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist() {
        // Given
        given(storeRepository.existsById(99L)).willReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> storeService.deleteStore(99L));
        verify(storeRepository, never()).deleteById(anyLong());
    }
} 