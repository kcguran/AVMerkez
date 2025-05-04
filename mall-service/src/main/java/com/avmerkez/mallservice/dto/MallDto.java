package com.avmerkez.mallservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MallDto {
    private Long id;
    private String name;
    private String city;
    private String district;
    // İhtiyaç duyulan diğer alanlar eklenebilir (adres vb.)
} 