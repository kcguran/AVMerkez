package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.dto.CreateStoreRequest;
import com.avmerkez.storeservice.dto.StoreDto;
import com.avmerkez.storeservice.dto.UpdateStoreRequest;
import com.avmerkez.storeservice.entity.Store;
import com.avmerkez.storeservice.exception.ResourceNotFoundException; // Reuse exception
import com.avmerkez.storeservice.mapper.StoreMapper;
import com.avmerkez.storeservice.repository.StoreRepository;
import com.avmerkez.storeservice.client.MallServiceClient;
import feign.FeignException;
import com.avmerkez.storeservice.exception.InvalidInputException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;
    private final MallServiceClient mallServiceClient;

    @Override
    public StoreDto createStore(CreateStoreRequest createStoreRequest) {
        log.info("Attempting to create new store '{}' for mallId: {}", createStoreRequest.getName(), createStoreRequest.getMallId());

        validateMallExists(createStoreRequest.getMallId());

        Store store = storeMapper.createRequestToStore(createStoreRequest);
        Store savedStore = storeRepository.save(store);
        log.info("Successfully created store '{}' with id: {}", savedStore.getName(), savedStore.getId());
        return storeMapper.toStoreDto(savedStore);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreDto getStoreById(Long id) {
        log.info("Fetching store by id: {}", id);
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));
        return storeMapper.toStoreDto(store);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDto> getAllStores(Long mallId) {
        log.info("Fetching stores. Filter mallId: [{}]", mallId);
        List<Store> stores;
        if (mallId != null) {
            log.debug("Filtering stores by mallId: {}", mallId);
            stores = storeRepository.findByMallId(mallId);
        } else {
            log.debug("Fetching all stores without mallId filter.");
            stores = storeRepository.findAll();
        }
        return storeMapper.toStoreDtoList(stores);
    }

    @Override
    @Transactional
    public StoreDto updateStore(Long id, UpdateStoreRequest updateStoreRequest) {
        log.info("Attempting to update store with id: {}", id);
        Store existingStore = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));

        storeMapper.updateStoreFromRequest(updateStoreRequest, existingStore);
        Store updatedStore = storeRepository.save(existingStore);
        log.info("Successfully updated store with id: {}", updatedStore.getId());
        return storeMapper.toStoreDto(updatedStore);
    }

    @Override
    public void deleteStore(Long id) {
        log.info("Deleting store with id: {}", id);
        if (!storeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Store", "id", id);
        }
        storeRepository.deleteById(id);
    }

    /**
     * Helper method to validate if a mall exists using MallServiceClient.
     * Throws InvalidInputException if the mall does not exist (Feign 404).
     * Throws RuntimeException for other Feign errors.
     */
    private void validateMallExists(Long mallId) {
        if (mallId == null) {
             throw new InvalidInputException("Mall ID cannot be null.");
        }
        try {
            log.debug("Checking existence of mallId: {}", mallId);
            ResponseEntity<Void> response = mallServiceClient.checkMallExists(mallId);
            // If call succeeds (2xx), mall exists.
            log.debug("Mall check successful for mallId: {}. Status: {}", mallId, response.getStatusCode());
        } catch (FeignException.NotFound e) {
            log.warn("Mall validation failed. Mall with id {} not found.", mallId);
            throw new InvalidInputException("Invalid Mall ID: " + mallId + ". Mall not found.", e);
        } catch (FeignException e) {
            log.error("Error during Feign call to mall-service for mallId: {}. Status: {}, Message: {}", mallId, e.status(), e.getMessage());
            // Handle other Feign errors (e.g., 5xx, connection refused)
            throw new RuntimeException("Failed to validate Mall ID due to communication error with mall-service.", e);
        }
    }
} 