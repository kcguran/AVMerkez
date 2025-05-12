package com.avmerkez.storeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for updating an existing category")
public class UpdateCategoryRequest {

    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    @Schema(description = "New name of the category. If not provided, current name will be kept.", example = "Consumer Electronics", nullable = true)
    private String name;

    @Schema(description = "New ID of the parent category. Use null to make it a root category.", example = "2", nullable = true)
    private Long parentId;

    @Schema(description = "New URL or path to the category icon/image. Provide an empty string to remove.", example = "/images/consumer_electronics.png", nullable = true)
    private String iconUrl;
} 