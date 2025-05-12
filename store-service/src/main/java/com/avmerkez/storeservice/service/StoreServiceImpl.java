package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.client.MallServiceClient;
import com.avmerkez.storeservice.dto.CreateStoreRequest;
import com.avmerkez.storeservice.dto.StoreDetailDto;
import com.avmerkez.storeservice.dto.StoreDto;
import com.avmerkez.storeservice.dto.UpdateStoreRequest;
import com.avmerkez.storeservice.entity.Brand;
import com.avmerkez.storeservice.entity.Category;
import com.avmerkez.storeservice.entity.Store;
import com.avmerkez.storeservice.exception.EntityNotFoundException;
import com.avmerkez.storeservice.exception.InvalidInputException;
import com.avmerkez.storeservice.exception.ServiceUnavailableException;
import com.avmerkez.storeservice.mapper.StoreMapper;
import com.avmerkez.storeservice.repository.BrandRepository;
import com.avmerkez.storeservice.repository.CategoryRepository;
import com.avmerkez.storeservice.repository.StoreRepository;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final StoreMapper storeMapper;
    private final MallServiceClient mallServiceClient;

    private static final String MALL_SERVICE_CLIENT_NAME = "mallServiceClient";

    @CircuitBreaker(name = MALL_SERVICE_CLIENT_NAME, fallbackMethod = "fallbackValidateMallExists")
    protected void validateMallExists(Long mallId) {
        if (mallId == null) {
            log.warn("Mall ID is null, skipping validation.");
            throw new InvalidInputException("Mall ID cannot be null.");
        }
        try {
            log.debug("Checking existence of mallId: {}", mallId);
            mallServiceClient.checkMallExists(mallId);
            log.info("Mall check successful for mallId: {}.", mallId);
        } catch (FeignException.NotFound e) {
            log.warn("Mall validation failed. Mall with id {} not found.", mallId, e);
            throw new InvalidInputException("Invalid Mall ID: " + mallId + ". Mall not found.");
        } catch (FeignException e) {
            log.error("Error during Feign call to mall-service for mallId: {}. Status: {}, Message: {}", mallId, e.status(), e.getMessage(), e);
            throw e;
        }
    }

    protected void fallbackValidateMallExists(Long mallId, Throwable t) {
        log.warn("Fallback for {}.checkMallExists(mallId={}) due to: {}: {}", MALL_SERVICE_CLIENT_NAME, mallId, t.getClass().getSimpleName(), t.getMessage());
        throw new ServiceUnavailableException("Mall service is currently unavailable. Cannot validate mall ID: " + mallId, t);
    }

    @Override
    @Transactional
    public StoreDetailDto createStore(CreateStoreRequest createStoreRequest) {
        validateMallExists(createStoreRequest.getMallId());

        Category category = categoryRepository.findById(createStoreRequest.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + createStoreRequest.getCategoryId()));
        Brand brand = brandRepository.findById(createStoreRequest.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException("Brand not found with id: " + createStoreRequest.getBrandId()));

        Store store = storeMapper.toStore(createStoreRequest);
        store.setCategory(category);
        store.setBrand(brand);

        Store savedStore = storeRepository.save(store);
        log.info("Created store '{}' with id {} for mallId {}.", savedStore.getName(), savedStore.getId(), savedStore.getMallId());
        return storeMapper.toStoreDetailDto(savedStore);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreDetailDto getStoreById(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found with id: " + storeId));
        return storeMapper.toStoreDetailDto(store);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDetailDto> getAllStoresDetail() {
        return storeMapper.toStoreDetailDtoList(storeRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDto> getStoresByMallId(Long mallId) {
        // Optional: validateMallExists(mallId); if we want to ensure mall exists even when just listing its stores.
        // However, if mall-service is down, this would prevent listing existing stores.
        // For now, we allow listing stores even if mall validation fails here, assuming data integrity.
        return storeMapper.toStoreDtoList(storeRepository.findByMallId(mallId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDto> getStoresByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category not found with id: " + categoryId);
        }
        return storeMapper.toStoreDtoList(storeRepository.findByCategoryId(categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDto> getStoresByBrandId(Long brandId) {
        if (!brandRepository.existsById(brandId)) {
            throw new EntityNotFoundException("Brand not found with id: " + brandId);
        }
        return storeMapper.toStoreDtoList(storeRepository.findByBrandId(brandId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDto> getStoresByMallAndCategoryId(Long mallId, Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category not found with id: " + categoryId);
        }
        // Optional: validateMallExists(mallId);
        return storeMapper.toStoreDtoList(storeRepository.findByMallIdAndCategoryId(mallId, categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDto> getStoresByMallAndBrandId(Long mallId, Long brandId) {
        if (!brandRepository.existsById(brandId)) {
            throw new EntityNotFoundException("Brand not found with id: " + brandId);
        }
        // Optional: validateMallExists(mallId);
        return storeMapper.toStoreDtoList(storeRepository.findByMallIdAndBrandId(mallId, brandId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDto> getAllStores() {
        log.debug("Retrieving all stores (basic DTO).");
        return storeMapper.toStoreDtoList(storeRepository.findAll());
    }

    @Override
    @Transactional
    public StoreDetailDto updateStore(Long storeId, UpdateStoreRequest updateStoreRequest) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found with id: " + storeId));

        if (updateStoreRequest.getMallId() != null && !updateStoreRequest.getMallId().equals(store.getMallId())) {
            validateMallExists(updateStoreRequest.getMallId());
            // If mallId is changed, it is updated by the mapper directly if `mallId` field is present in `updateStoreFromRequest` mapping
            // If not, set it manually: store.setMallId(updateStoreRequest.getMallId());
        }

        if (updateStoreRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateStoreRequest.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + updateStoreRequest.getCategoryId()));
            store.setCategory(category);
        }

        if (updateStoreRequest.getBrandId() != null) {
            Brand brand = brandRepository.findById(updateStoreRequest.getBrandId())
                    .orElseThrow(() -> new EntityNotFoundException("Brand not found with id: " + updateStoreRequest.getBrandId()));
            store.setBrand(brand);
        }

        storeMapper.updateStoreFromRequest(updateStoreRequest, store);
        Store updatedStore = storeRepository.save(store);
        log.info("Updated store '{}' with id {}.", updatedStore.getName(), updatedStore.getId());
        return storeMapper.toStoreDetailDto(updatedStore);
    }

    @Override
    @Transactional
    public void deleteStore(Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new EntityNotFoundException("Store not found with id: " + storeId);
        }
        storeRepository.deleteById(storeId);
        log.info("Deleted store with id {}.", storeId);
    }

    @Override
    public boolean checkStoreExists(Long id) {
        log.debug("Checking existence of store with id: {}", id);
        boolean exists = storeRepository.existsById(id);
        log.debug("Store with id {} exists: {}", id, exists);
        return exists;
    }

    private void validateCategoryExists(Long categoryId) {
        // ... existing code ...
    }
} 