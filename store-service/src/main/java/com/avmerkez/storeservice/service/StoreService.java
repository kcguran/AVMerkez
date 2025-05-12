package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.dto.CreateStoreRequest;
import com.avmerkez.storeservice.dto.StoreDetailDto;
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
    StoreDetailDto createStore(CreateStoreRequest createStoreRequest);

    /**
     * Retrieves a store by its ID.
     *
     * @param storeId The ID of the store to retrieve.
     * @return The found StoreDto.
     * @throws com.avmerkez.storeservice.exception.ResourceNotFoundException if store not found.
     */
    StoreDetailDto getStoreById(Long storeId);

    /**
     * Retrieves all stores, optionally filtered by mallId.
     *
     * @param mallId Optional mall ID filter.
     * @return A list of StoreDto objects.
     */
    List<StoreDto> getStoresByMallId(Long mallId);

    /**
     * Retrieves all stores, optionally filtered by categoryId.
     *
     * @param categoryId Optional category ID filter.
     * @return A list of StoreDto objects.
     */
    List<StoreDto> getStoresByCategoryId(Long categoryId);

    /**
     * Retrieves all stores, optionally filtered by brandId.
     *
     * @param brandId Optional brand ID filter.
     * @return A list of StoreDto objects.
     */
    List<StoreDto> getStoresByBrandId(Long brandId);

    /**
     * Retrieves all stores, optionally filtered by mallId and categoryId.
     *
     * @param mallId Optional mall ID filter.
     * @param categoryId Optional category ID filter.
     * @return A list of StoreDto objects.
     */
    List<StoreDto> getStoresByMallAndCategoryId(Long mallId, Long categoryId);

    /**
     * Retrieves all stores, optionally filtered by mallId and brandId.
     *
     * @param mallId Optional mall ID filter.
     * @param brandId Optional brand ID filter.
     * @return A list of StoreDto objects.
     */
    List<StoreDto> getStoresByMallAndBrandId(Long mallId, Long brandId);

    /**
     * Retrieves all stores, optionally filtered by mallId.
     *
     * @param mallId Optional mall ID filter.
     * @return A list of StoreDto objects.
     */
    List<StoreDetailDto> getAllStoresDetail();

    /**
     * Retrieves all stores.
     *
     * @return A list of StoreDto objects.
     */
    List<StoreDto> getAllStores();

    /**
     * Updates an existing store.
     *
     * @param storeId The ID of the store to update.
     * @param updateStoreRequest DTO containing store update data.
     * @return The updated StoreDto.
     * @throws com.avmerkez.storeservice.exception.ResourceNotFoundException if store not found.
     * TODO: Add validation for mallId using MallServiceClient if mallId is updated (or always check?)
     */
    StoreDetailDto updateStore(Long storeId, UpdateStoreRequest updateStoreRequest);

    /**
     * Deletes a store by its ID.
     *
     * @param storeId The ID of the store to delete.
     * @throws com.avmerkez.storeservice.exception.ResourceNotFoundException if store not found.
     */
    void deleteStore(Long storeId);

    /**
     * Checks if a store exists by its ID.
     *
     * @param id The ID of the store to check.
     * @return true if the store exists, false otherwise.
     */
    boolean checkStoreExists(Long id);
} 