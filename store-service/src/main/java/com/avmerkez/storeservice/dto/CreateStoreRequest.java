package com.avmerkez.storeservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStoreRequest {

    @NotNull(message = "Mall ID cannot be null")
    private Long mallId;

    @NotBlank(message = "Store name cannot be blank")
    private String name;

    private String category;

    @NotBlank(message = "Floor cannot be blank")
    private String floor;

    private String storeNumber;

    // private Long brandId;
    // private String contactInfo;
    // private String description;
    // private String logoUrl;
} 