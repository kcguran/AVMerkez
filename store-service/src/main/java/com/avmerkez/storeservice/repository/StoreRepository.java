package com.avmerkez.storeservice.repository;

import com.avmerkez.storeservice.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    // Belirli bir AVM'deki mağazaları bulmak için
    List<Store> findByMallId(Long mallId);

    // İleride kategoriye, markaya göre filtreleme eklenebilir
    // List<Store> findByMallIdAndCategoryId(Long mallId, Long categoryId);
} 