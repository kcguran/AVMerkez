package com.avmerkez.storeservice.repository;

import com.avmerkez.storeservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    List<Category> findByParentCategoryIsNull(); // Kök kategorileri bulmak için
    List<Category> findByParentCategoryId(Long parentCategoryId); // Alt kategorileri bulmak için
} 