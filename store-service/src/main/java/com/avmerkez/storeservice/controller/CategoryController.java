package com.avmerkez.storeservice.controller;

import com.avmerkez.storeservice.dto.CategoryDto;
import com.avmerkez.storeservice.dto.CreateCategoryRequest;
import com.avmerkez.storeservice.dto.GenericApiResponse;
import com.avmerkez.storeservice.dto.UpdateCategoryRequest;
import com.avmerkez.storeservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "APIs for managing product/store categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new category")
    @ApiResponse(responseCode = "201", description = "Category created successfully",
            content = @Content(schema = @Schema(implementation = GenericApiResponse.class)))
    public ResponseEntity<GenericApiResponse<CategoryDto>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryDto createdCategory = categoryService.createCategory(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdCategory.getId()).toUri();
        return ResponseEntity.created(location).body(GenericApiResponse.success(createdCategory, "Category created successfully."));
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    @ApiResponse(responseCode = "200", description = "List of categories",
            content = @Content(schema = @Schema(implementation = GenericApiResponse.class)))
    public ResponseEntity<GenericApiResponse<List<CategoryDto>>> getAllCategories(
            @Parameter(description = "Filter by parent category ID") @RequestParam(required = false) Long parentId) {
        List<CategoryDto> categories;
        if (parentId == null) {
            categories = categoryService.getRootCategories();
        } else {
            categories = categoryService.getSubcategories(parentId);
        }
        return ResponseEntity.ok(GenericApiResponse.success(categories));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category found",
            content = @Content(schema = @Schema(implementation = GenericApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<GenericApiResponse<CategoryDto>> getCategoryById(@PathVariable Long id) {
        CategoryDto categoryDto = categoryService.getCategoryById(id);
        return ResponseEntity.ok(GenericApiResponse.success(categoryDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category updated successfully",
            content = @Content(schema = @Schema(implementation = GenericApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<GenericApiResponse<CategoryDto>> updateCategory(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryDto updatedCategory = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(GenericApiResponse.success(updatedCategory, "Category updated successfully."));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category by ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Category deleted successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
} 