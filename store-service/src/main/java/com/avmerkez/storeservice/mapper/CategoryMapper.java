package com.avmerkez.storeservice.mapper;

import com.avmerkez.storeservice.dto.CategoryDto;
import com.avmerkez.storeservice.dto.CreateCategoryRequest;
import com.avmerkez.storeservice.dto.UpdateCategoryRequest;
import com.avmerkez.storeservice.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    @Mapping(source = "parentCategory.id", target = "parentId")
    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toCategoryDtoList(List<Category> categories);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentCategory", ignore = true) // Will be handled in service layer
    Category toCategory(CreateCategoryRequest createCategoryRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentCategory", ignore = true) // Will be handled in service layer
    void updateCategoryFromRequest(UpdateCategoryRequest updateCategoryRequest, @MappingTarget Category category);
} 