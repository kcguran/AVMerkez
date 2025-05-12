package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.dto.CategoryDto;
import com.avmerkez.storeservice.dto.CreateCategoryRequest;
import com.avmerkez.storeservice.dto.UpdateCategoryRequest;
import com.avmerkez.storeservice.entity.Category;
import com.avmerkez.storeservice.exception.DuplicateEntityException;
import com.avmerkez.storeservice.exception.EntityNotFoundException;
import com.avmerkez.storeservice.mapper.CategoryMapper;
import com.avmerkez.storeservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest createCategoryRequest) {
        categoryRepository.findByName(createCategoryRequest.getName()).ifPresent(c -> {
            throw new DuplicateEntityException("Category with name '" + createCategoryRequest.getName() + "' already exists.");
        });

        Category category = categoryMapper.toCategory(createCategoryRequest);
        if (createCategoryRequest.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(createCategoryRequest.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found with id: " + createCategoryRequest.getParentId()));
            category.setParentCategory(parentCategory);
        }
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryMapper.toCategoryDtoList(categoryRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getRootCategories() {
        return categoryMapper.toCategoryDtoList(categoryRepository.findByParentCategoryIsNull());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getSubcategories(Long parentCategoryId) {
        if (!categoryRepository.existsById(parentCategoryId)) {
            throw new EntityNotFoundException("Parent category not found with id: " + parentCategoryId);
        }
        return categoryMapper.toCategoryDtoList(categoryRepository.findByParentCategoryId(parentCategoryId));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long categoryId, UpdateCategoryRequest updateCategoryRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));

        // Check for name conflict if name is being changed
        if (updateCategoryRequest.getName() != null && !updateCategoryRequest.getName().equals(category.getName())) {
            categoryRepository.findByName(updateCategoryRequest.getName()).ifPresent(c -> {
                throw new DuplicateEntityException("Category with name '" + updateCategoryRequest.getName() + "' already exists.");
            });
        }

        categoryMapper.updateCategoryFromRequest(updateCategoryRequest, category);

        if (updateCategoryRequest.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(updateCategoryRequest.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found with id: " + updateCategoryRequest.getParentId()));
            category.setParentCategory(parentCategory);
        } else {
            // If parentCategoryId is explicitly set to null (or not provided and it was null already)
            // For MapStruct to ignore if null, we might need to handle it here for removal
            if (category.getParentCategory() != null) {
                 // This part depends on how nulls are handled for parentCategoryId in UpdateCategoryRequest
                 // If parentCategoryId is not in the request, it won't be set to null by default by mapper
                 // To explicitly set it to null, the request must allow sending null or a specific value for it.
                 // For now, we assume if it's null in request, it means remove parent.
                category.setParentCategory(null);
            }
        }

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category not found with id: " + categoryId);
        }
        // TODO: Add logic to handle subcategories or stores referencing this category before deletion
        // For now, simple deletion.
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean categoryExists(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }
} 