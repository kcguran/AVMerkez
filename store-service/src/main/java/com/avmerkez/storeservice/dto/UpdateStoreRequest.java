package com.avmerkez.storeservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoreRequest {
    // Güncelleme için alanlar
    private String name;
    private Long categoryId;
    private String floor;
    private String storeNumber;

    // private Long brandId;
    // private String contactInfo;
    // private String description;
    // private String logoUrl;
} 