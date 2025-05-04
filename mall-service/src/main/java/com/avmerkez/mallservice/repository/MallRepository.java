package com.avmerkez.mallservice.repository;

import com.avmerkez.mallservice.entity.Mall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MallRepository extends JpaRepository<Mall, Long> {
    // İleride özel sorgular gerekirse buraya eklenebilir
    // Örneğin: List<Mall> findByCity(String city);
} 