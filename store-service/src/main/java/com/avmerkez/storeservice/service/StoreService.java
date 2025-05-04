package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.dto.CreateStoreRequest;
import com.avmerkez.storeservice.dto.StoreDto;
import com.avmerkez.storeservice.dto.UpdateStoreRequest;

import java.util.List;

public interface StoreService {
    StoreDto createStore(CreateStoreRequest createStoreRequest);
    StoreDto getStoreById(Long id);
    List<StoreDto> getAllStores(Long mallId); // AVM ID'ye göre filtreleme ekleyelim
    List<StoreDto> getStoresByMallId(Long mallId); // Eksik metodu ekle
    StoreDto updateStore(Long id, UpdateStoreRequest updateStoreRequest);
    void deleteStore(Long id);
} 