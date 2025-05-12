package com.avmerkez.storeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for detailed Store information, including full Category and Brand details")
public class StoreDetailDto {
    @Schema(description = "Unique identifier of the store", example = "1001")
    private Long id;

    @Schema(description = "Name of the store", example = "Tech Store Plus")
    private String name;

    @Schema(description = "ID of the mall where the store is located", example = "1")
    private Long mallId;

    @Schema(description = "Floor where the store is located", example = "1st Floor")
    private String floor;

    @Schema(description = "Store number or unit", example = "A-105")
    private String storeNo;

    @Schema(description = "Full details of the category this store belongs to")
    private CategoryDto category;

    @Schema(description = "Full details of the brand for this store (if applicable)", nullable = true)
    private BrandDto brand;

    @Schema(description = "Contact information for the store (e.g., phone, email)", example = "Phone: 555-1234", nullable = true)
    private String contactInformation;

    @Schema(description = "A brief description of the store or its offerings", example = "Latest gadgets and electronics.", nullable = true)
    private String description;

    @Schema(description = "URL of the store's logo", example = "https://cdn.example.com/tech_store_logo.png", nullable = true)
    private String logoUrl;
} 