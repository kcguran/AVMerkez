package com.avmerkez.storeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for basic Store details")
public class StoreDto {
    @Schema(description = "Unique identifier of the store", example = "1001")
    private Long id;

    @Schema(description = "Name of the store", example = "Tech Store Plus")
    private String name;

    @Schema(description = "ID of the mall where the store is located", example = "1")
    private Long mallId;

    @Schema(description = "ID of the category this store belongs to", example = "1")
    private Long categoryId;

    @Schema(description = "Name of the category this store belongs to", example = "Electronics")
    private String categoryName; // Denormalized for convenience

    @Schema(description = "ID of the brand for this store (if applicable)", example = "101", nullable = true)
    private Long brandId;

    @Schema(description = "Name of the brand for this store (if applicable)", example = "Sony", nullable = true)
    private String brandName; // Denormalized for convenience

    @Schema(description = "Floor where the store is located", example = "1st Floor")
    private String floor;

    @Schema(description = "Store number or unit", example = "A-105")
    private String storeNo;

    @Schema(description = "Contact information for the store", example = "Phone: 555-1234, Email: info@techstore.com", nullable = true)
    private String contactInformation;

    @Schema(description = "A brief description of the store", example = "Your one-stop shop for all electronics.", nullable = true)
    private String description;

    @Schema(description = "URL of the store's logo", example = "https://cdn.example.com/tech_store_logo.png", nullable = true)
    private String logoUrl;
} 