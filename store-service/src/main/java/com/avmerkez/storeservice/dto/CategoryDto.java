package com.avmerkez.storeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Category details")
public class CategoryDto {
    @Schema(description = "Unique identifier of the category", example = "1")
    private Long id;

    @Schema(description = "Name of the category", example = "Electronics")
    private String name;

    @Schema(description = "ID of the parent category, if this is a subcategory. Null for root categories.", example = "null", nullable = true)
    private Long parentId;

    @Schema(description = "URL or path to the category icon/image", example = "/images/electronics.png", nullable = true)
    private String iconUrl;
} 