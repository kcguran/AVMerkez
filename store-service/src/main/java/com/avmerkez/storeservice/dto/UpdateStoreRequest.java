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
    // categoryId yerine category
    private String category;
    private String floor;
    // storeNo yerine storeNumber
    private String storeNumber;

    // private Long brandId;
    // private String contactInfo;
    // private String description;
    // private String logoUrl;
} 