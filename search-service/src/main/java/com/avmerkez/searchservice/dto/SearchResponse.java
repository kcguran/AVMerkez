package com.avmerkez.searchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    private List<Object> results;
    private long total;
    private Map<String, Long> cityCounts;
    private Map<String, Long> categoryCounts;
} 