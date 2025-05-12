package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.dto.CategoryDto;
import com.avmerkez.storeservice.dto.CreateCategoryRequest;
import com.avmerkez.storeservice.dto.UpdateCategoryRequest;
import com.avmerkez.storeservice.entity.Category;
import com.avmerkez.storeservice.exception.DuplicateEntityException;
import com.avmerkez.storeservice.exception.EntityNotFoundException;
import com.avmerkez.storeservice.mapper.CategoryMapper;
import com.avmerkez.storeservice.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDto categoryDto;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        category = new Category("Electronics");
        category.setId(1L);

        categoryDto = new CategoryDto(1L, "Electronics", null, null);

        createRequest = new CreateCategoryRequest();
        createRequest.setName("Electronics");

        updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Digital Goods");
    }

    @Test
    @DisplayName("createCategory should save and return DTO")
    void createCategory_ValidRequest_ReturnsCategoryDto() {
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.empty());
        when(categoryMapper.toCategory(createRequest)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toCategoryDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.createCategory(createRequest);

        assertThat(result).isEqualTo(categoryDto);
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("createCategory with existing name should throw DuplicateEntityException")
    void createCategory_ExistingName_ThrowsDuplicateEntityException() {
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));

        assertThrows(DuplicateEntityException.class, () -> categoryService.createCategory(createRequest));
        verify(categoryRepository, never()).save(any(Category.class));
    }
    
    @Test
    @DisplayName("createCategory with parentId should set parentCategory")
    void createCategory_WithParentId_SetsParentCategory() {
        CreateCategoryRequest subCategoryRequest = new CreateCategoryRequest();
        subCategoryRequest.setName("Laptops");
        Long parentId = 1L;

        Category parentCategory = new Category("Electronics");
        parentCategory.setId(1L);
        Category subCategoryEntity = new Category("Laptops");
        
        CategoryDto subCategoryDto = new CategoryDto(2L, "Laptops", null, null);

        when(categoryRepository.findByName("Laptops")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryMapper.toCategory(subCategoryRequest)).thenReturn(subCategoryEntity);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category saved = invocation.getArgument(0);
            saved.setId(2L); // Simulate save
            saved.setParentCategory(parentCategory); // Simulate parent set by service
            return saved;
        });
        when(categoryMapper.toCategoryDto(any(Category.class))).thenReturn(subCategoryDto);

        CategoryDto result = categoryService.createCategory(subCategoryRequest);

        verify(categoryRepository).findById(eq(parentId));
        verify(categoryRepository).save(argThat(cat -> 
            cat.getParentCategory() != null && 
            cat.getParentCategory().getId().equals(1L)
        ));
    }

    @Test
    @DisplayName("getCategoryById should return DTO when found")
    void getCategoryById_Exists_ReturnsCategoryDto() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.getCategoryById(1L);

        assertThat(result).isEqualTo(categoryDto);
    }

    @Test
    @DisplayName("getCategoryById should throw EntityNotFoundException when not found")
    void getCategoryById_NotExists_ThrowsEntityNotFoundException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    @DisplayName("updateCategory should update and return DTO")
    void updateCategory_ValidRequest_ReturnsUpdatedCategoryDto() {
        Category updatedEntity = new Category("Digital Goods");
        updatedEntity.setId(1L);
        CategoryDto updatedDto = new CategoryDto(1L, "Digital Goods", null, null);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName("Digital Goods")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedEntity); // mock save to return the state after mapper call
        when(categoryMapper.toCategoryDto(updatedEntity)).thenReturn(updatedDto);
        // Simulate void method behavior for mapper
        doNothing().when(categoryMapper).updateCategoryFromRequest(updateRequest, category);

        CategoryDto result = categoryService.updateCategory(1L, updateRequest);
        
        // Verify mapper was called before save
        verify(categoryMapper).updateCategoryFromRequest(updateRequest, category);
        verify(categoryRepository).save(category);
        assertThat(result.getName()).isEqualTo("Digital Goods");
    }

    @Test
    @DisplayName("deleteCategory should call deleteById when exists")
    void deleteCategory_Exists_CallsDeleteById() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteCategory should throw EntityNotFoundException when not exists")
    void deleteCategory_NotExists_ThrowsEntityNotFoundException() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("getRootCategories should return list of root categories")
    void getRootCategories_ReturnsRootCategoryDtoList() {
        when(categoryRepository.findByParentCategoryIsNull()).thenReturn(Collections.singletonList(category));
        when(categoryMapper.toCategoryDtoList(Collections.singletonList(category))).thenReturn(Collections.singletonList(categoryDto));

        List<CategoryDto> results = categoryService.getRootCategories();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Electronics");
        verify(categoryRepository).findByParentCategoryIsNull();
    }

    @Test
    @DisplayName("getSubcategories should return list of subcategories")
    void getSubcategories_ValidParentId_ReturnsSubCategoryDtoList() {
        Long parentId = 1L;
        Category subCategory = new Category("Laptops");
        subCategory.setId(2L);
        subCategory.setParentCategory(category);
        CategoryDto subCategoryDto = new CategoryDto(2L, "Laptops", null, null);

        when(categoryRepository.existsById(parentId)).thenReturn(true);
        when(categoryRepository.findByParentCategoryId(parentId)).thenReturn(Collections.singletonList(subCategory));
        when(categoryMapper.toCategoryDtoList(Collections.singletonList(subCategory))).thenReturn(Collections.singletonList(subCategoryDto));

        List<CategoryDto> results = categoryService.getSubcategories(parentId);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Laptops");
        verify(categoryRepository).findByParentCategoryId(parentId);
    }

    @Test
    @DisplayName("getSubcategories should throw EntityNotFoundException if parent does not exist")
    void getSubcategories_InvalidParentId_ThrowsEntityNotFoundException() {
        Long parentId = 99L;
        when(categoryRepository.existsById(parentId)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> categoryService.getSubcategories(parentId));
        verify(categoryRepository, never()).findByParentCategoryId(anyLong());
    }
} 