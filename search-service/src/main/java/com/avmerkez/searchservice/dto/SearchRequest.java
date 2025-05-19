package com.avmerkez.searchservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchRequest {
    @NotBlank(message = "Arama sorgusu boş olamaz")
    private String query;
    private String city;
    private String category;
    private Long userId;
    private Boolean onlyFavorites;
    private Boolean suggest;
} 