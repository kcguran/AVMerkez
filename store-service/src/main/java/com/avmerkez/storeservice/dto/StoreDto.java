package com.avmerkez.storeservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {
    private Long id;
    private Long mallId;
    private String name;
    private Long categoryId;
    private String floor;
    private String storeNumber;
} 