package com.avmerkez.storeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for updating an existing store")
public class UpdateStoreRequest {
    @Size(min = 2, max = 150, message = "Store name must be between 2 and 150 characters")
    @Schema(description = "New name of the store. If not provided, current name will be kept.", example = "Gadget Universe", nullable = true)
    private String name;

    @Schema(description = "New mall ID for the store. If not provided, current mall ID will be kept.", example = "2", nullable = true)
    private Long mallId; // Usually not updatable directly, or with care

    @Schema(description = "New category ID for the store. If not provided, current category ID will be kept.", example = "2", nullable = true)
    private Long categoryId;

    @Schema(description = "New brand ID for the store. Can be null to remove brand association. If not provided, current brand ID will be kept.", example = "103", nullable = true)
    private Long brandId;

    @Size(max = 50, message = "Floor designation cannot exceed 50 characters")
    @Schema(description = "New floor for the store. If not provided, current floor will be kept.", example = "2nd Level", nullable = true)
    private String floor;

    @Size(max = 50, message = "Store number cannot exceed 50 characters")
    @Schema(description = "New store number. If not provided, current store number will be kept.", example = "B-210", nullable = true)
    private String storeNo;

    @Size(max = 255, message = "Contact information cannot exceed 255 characters")
    @Schema(description = "New contact information. Provide an empty string to remove. If not provided, current contact info will be kept.", example = "(555) 111-2222", nullable = true)
    private String contactInformation;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(description = "New description for the store. Provide an empty string to remove. If not provided, current description will be kept.", example = "The ultimate destination for tech enthusiasts!", nullable = true)
    private String description;

    @Size(max = 255, message = "Logo URL cannot exceed 255 characters")
    @Schema(description = "New URL for the store's logo. Provide an empty string to remove. If not provided, current logo URL will be kept.", example = "https://cdn.example.com/gadget_universe_logo.png", nullable = true)
    private String logoUrl;
} 