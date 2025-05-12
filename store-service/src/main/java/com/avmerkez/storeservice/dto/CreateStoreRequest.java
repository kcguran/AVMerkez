package com.avmerkez.storeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating a new store")
public class CreateStoreRequest {

    @NotNull(message = "Mall ID cannot be null")
    @Schema(description = "ID of the mall where the store will be located", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long mallId;

    @NotBlank(message = "Store name cannot be blank")
    @Size(min = 2, max = 150, message = "Store name must be between 2 and 150 characters")
    @Schema(description = "Name of the new store", example = "Gadget World", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message = "Category ID cannot be null")
    @Schema(description = "ID of the category this store belongs to", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @Size(max = 50, message = "Floor designation cannot exceed 50 characters")
    @Schema(description = "Floor where the store is located (e.g., Ground Floor, Level 2)", example = "Ground Floor", nullable = true)
    private String floor;

    @Size(max = 50, message = "Store number cannot exceed 50 characters")
    @Schema(description = "Store number or unit identifier (e.g., G-12, Unit 205B)", example = "G-12", nullable = true)
    private String storeNo;

    @Schema(description = "ID of the brand for this store (optional). Omit if not applicable or a generic store.", example = "102", nullable = true)
    private Long brandId;

    @Size(max = 255, message = "Contact information cannot exceed 255 characters")
    @Schema(description = "Contact information for the store (e.g., phone number, email address)", example = "(555) 987-6543", nullable = true)
    private String contactInformation;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(description = "A brief description of the store or its main offerings", example = "Your one-stop shop for all cool gadgets!", nullable = true)
    private String description;

    @Size(max = 255, message = "Logo URL cannot exceed 255 characters")
    @Schema(description = "URL of the store's logo", example = "https://cdn.example.com/gadget_world_logo.png", nullable = true)
    private String logoUrl;
} 