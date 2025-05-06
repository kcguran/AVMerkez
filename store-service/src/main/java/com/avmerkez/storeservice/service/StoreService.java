package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.dto.CreateStoreRequest;
import com.avmerkez.storeservice.dto.StoreDto;
import com.avmerkez.storeservice.dto.UpdateStoreRequest;

import java.util.List;

/**
 * Service interface for managing Stores.
 */
public interface StoreService {

    /**
     * Creates a new store.
     *
     * @param createStoreRequest DTO containing store creation data.
     * @return The created StoreDto.
     * TODO: Add validation for mallId using MallServiceClient
     */
    StoreDto createStore(CreateStoreRequest createStoreRequest);

    /**
     * Retrieves a store by its ID.
     *
     * @param id The ID of the store to retrieve.
     * @return The found StoreDto.
     * @throws com.avmerkez.storeservice.exception.ResourceNotFoundException if store not found.
     */
    StoreDto getStoreById(Long id);

    /**
     * Retrieves all stores, optionally filtered by mallId.
     *
     * @param mallId Optional mall ID filter.
     * @return A list of StoreDto objects.
     */
    List<StoreDto> getAllStores(Long mallId);

    /**
     * Updates an existing store.
     *
     * @param id The ID of the store to update.
     * @param updateStoreRequest DTO containing store update data.
     * @return The updated StoreDto.
     * @throws com.avmerkez.storeservice.exception.ResourceNotFoundException if store not found.
     * TODO: Add validation for mallId using MallServiceClient if mallId is updated (or always check?)
     */
    StoreDto updateStore(Long id, UpdateStoreRequest updateStoreRequest);

    /**
     * Deletes a store by its ID.
     *
     * @param id The ID of the store to delete.
     * @throws com.avmerkez.storeservice.exception.ResourceNotFoundException if store not found.
     */
    void deleteStore(Long id);
} 