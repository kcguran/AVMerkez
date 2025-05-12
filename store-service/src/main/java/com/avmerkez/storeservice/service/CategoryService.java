package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.dto.CategoryDto;
import com.avmerkez.storeservice.dto.CreateCategoryRequest;
import com.avmerkez.storeservice.dto.UpdateCategoryRequest;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CreateCategoryRequest createCategoryRequest);
    CategoryDto getCategoryById(Long categoryId);
    List<CategoryDto> getAllCategories();
    List<CategoryDto> getRootCategories();
    List<CategoryDto> getSubcategories(Long parentCategoryId);
    CategoryDto updateCategory(Long categoryId, UpdateCategoryRequest updateCategoryRequest);
    void deleteCategory(Long categoryId);
    boolean categoryExists(Long categoryId);
} 