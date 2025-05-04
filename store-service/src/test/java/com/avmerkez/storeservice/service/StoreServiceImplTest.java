package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.dto.CreateStoreRequest;
import com.avmerkez.storeservice.dto.StoreDto;
import com.avmerkez.storeservice.dto.UpdateStoreRequest;
import com.avmerkez.storeservice.entity.Store;
import com.avmerkez.storeservice.exception.ResourceNotFoundException;
import com.avmerkez.storeservice.mapper.StoreMapper;
import com.avmerkez.storeservice.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    // TODO: Add mock for Feign Client later
    // @Mock
    // private MallServiceClient mallServiceClient;

    @InjectMocks // Inject mocks into this instance
    private StoreServiceImpl storeService;

    private Store store;
    private StoreDto storeDto;
    private CreateStoreRequest createStoreRequest;
    private UpdateStoreRequest updateStoreRequest;

    @BeforeEach
    void setUp() {
        store = Store.builder()
                .id(1L)
                .mallId(10L)
                .name("Test Store")
                .category("Test Category")
                .floor("Test Floor")
                .storeNumber("T-1")
                .build();

        storeDto = new StoreDto(1L, 10L, "Test Store", "Test Category", "Test Floor", "T-1");

        createStoreRequest = new CreateStoreRequest(10L, "New Store", "New Category", "New Floor", "N-1");
        updateStoreRequest = new UpdateStoreRequest("Updated Store", "Updated Category", "Updated Floor", "U-1");
    }

    @Test
    @DisplayName("Create store successfully")
    void createStore_ShouldReturnStoreDto() {
        // Given
        // TODO: Mock Feign client call later
        given(storeMapper.createRequestToStore(createStoreRequest)).willReturn(store); // Use store for simplicity here
        given(storeRepository.save(any(Store.class))).willReturn(store);
        given(storeMapper.toStoreDto(store)).willReturn(storeDto);

        // When
        StoreDto createdDto = storeService.createStore(createStoreRequest);

        // Then
        assertThat(createdDto).isNotNull();
        assertThat(createdDto.getName()).isEqualTo(storeDto.getName());
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    @DisplayName("Get store by existing ID")
    void getStoreById_ShouldReturnStoreDto_WhenIdExists() {
        // Given
        given(storeRepository.findById(1L)).willReturn(Optional.of(store));
        given(storeMapper.toStoreDto(store)).willReturn(storeDto);

        // When
        StoreDto foundDto = storeService.getStoreById(1L);

        // Then
        assertThat(foundDto).isNotNull();
        assertThat(foundDto.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Get store by non-existing ID")
    void getStoreById_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist() {
        // Given
        given(storeRepository.findById(99L)).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> storeService.getStoreById(99L));
        verify(storeMapper, never()).toStoreDto(any());
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
    @DisplayName("Update store successfully")
    void updateStore_ShouldReturnUpdatedStoreDto() {
        // Given
        // TODO: Mock Feign client call later
        given(storeRepository.findById(1L)).willReturn(Optional.of(store));
        given(storeRepository.save(any(Store.class))).willReturn(store); // Assume save returns the updated entity
        given(storeMapper.toStoreDto(store)).willReturn(storeDto); // Return the original DTO for simplicity in mock
        // Mock the mapper update method (it's void)
        willDoNothing().given(storeMapper).updateStoreFromRequest(any(UpdateStoreRequest.class), any(Store.class));

        // When
        StoreDto updatedDto = storeService.updateStore(1L, updateStoreRequest);

        // Then
        assertThat(updatedDto).isNotNull();
        // Verify mapper was called to update the entity
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
        verify(storeRepository, times(1)).existsById(99L);
        verify(storeRepository, never()).deleteById(anyLong());
    }
} 