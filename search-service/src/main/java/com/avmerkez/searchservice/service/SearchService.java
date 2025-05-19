package com.avmerkez.searchservice.service;

import com.avmerkez.searchservice.dto.SearchRequest;
import com.avmerkez.searchservice.dto.SearchResponse;

public interface SearchService {
    SearchResponse search(SearchRequest request);
} 