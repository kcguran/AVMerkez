package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.dto.CreateStoreRequest;
import com.avmerkez.storeservice.dto.StoreDto;
import com.avmerkez.storeservice.dto.UpdateStoreRequest;
import com.avmerkez.storeservice.entity.Store;
import com.avmerkez.storeservice.exception.ResourceNotFoundException; // Reuse exception
import com.avmerkez.storeservice.mapper.StoreMapper;
import com.avmerkez.storeservice.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;
    // TODO: mall-service ile iletişim kurmak için FeignClient veya RestTemplate eklenecek (mallId kontrolü için)

    @Override
    public StoreDto createStore(CreateStoreRequest createStoreRequest) {
        // TODO: createStoreRequest.getMallId() ile mall-service'e istek atıp AVM'nin varlığını kontrol et
        Store store = storeMapper.createRequestToStore(createStoreRequest);
        Store savedStore = storeRepository.save(store);
        return storeMapper.toStoreDto(savedStore);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreDto getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));
        return storeMapper.toStoreDto(store);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDto> getStoresByMallId(Long mallId) {
        List<Store> stores = storeRepository.findByMallId(mallId);
        return storeMapper.toStoreDtoList(stores);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDto> getAllStores(Long mallId) {
        List<Store> stores;
        if (mallId != null) {
            stores = storeRepository.findByMallId(mallId);
        } else {
            stores = storeRepository.findAll(); // Eğer mallId null ise hepsini getir (opsiyonel)
        }
        return storeMapper.toStoreDtoList(stores);
    }

    @Override
    @Transactional
    public StoreDto updateStore(Long id, UpdateStoreRequest updateStoreRequest) {
        Store existingStore = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));

        storeMapper.updateStoreFromRequest(updateStoreRequest, existingStore);

        Store updatedStore = storeRepository.save(existingStore);
        return storeMapper.toStoreDto(updatedStore);
    }

    @Override
    public void deleteStore(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Store", "id", id);
        }
        storeRepository.deleteById(id);
    }
} 