package com.avmerkez.storeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating a new category")
public class CreateCategoryRequest {
    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    @Schema(description = "Name of the new category", example = "Home Appliances", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "ID of the parent category, if this is a subcategory. Omit or null for a root category.", example = "1", nullable = true)
    private Long parentId;

    @Schema(description = "URL or path to the category icon/image", example = "/images/home_appliances.png", nullable = true)
    private String iconUrl;
} 