package com.avmerkez.searchservice.repository;

import com.avmerkez.searchservice.model.MallDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MallSearchRepository extends ElasticsearchRepository<MallDocument, Long> {
} 