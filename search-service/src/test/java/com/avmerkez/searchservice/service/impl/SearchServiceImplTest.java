package com.avmerkez.searchservice.service.impl;

import com.avmerkez.searchservice.client.UserServiceClient;
import com.avmerkez.searchservice.dto.SearchRequest;
import com.avmerkez.searchservice.dto.SearchResponse;
import com.avmerkez.searchservice.model.MallDocument;
import com.avmerkez.searchservice.repository.MallSearchRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SearchServiceImplTest {
    @Mock
    private MallSearchRepository mallSearchRepository;
    @Mock
    private ElasticsearchOperations elasticsearchOperations;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private SearchServiceImpl searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void search_onlyFavorites_userServiceReturnsFavorites() {
        SearchRequest request = new SearchRequest();
        request.setOnlyFavorites(true);
        request.setUserId(1L);
        when(userServiceClient.getFavoriteMallIds(1L)).thenReturn(Set.of(10L, 20L));
        when(elasticsearchOperations.search(any(), eq(MallDocument.class))).thenReturn(mock(SearchHits.class));
        SearchResponse response = searchService.search(request);
        verify(userServiceClient).getFavoriteMallIds(1L);
        assertThat(response).isNotNull();
    }

    @Test
    void search_onlyFavorites_userServiceUnavailable() {
        SearchRequest request = new SearchRequest();
        request.setOnlyFavorites(true);
        request.setUserId(2L);
        when(userServiceClient.getFavoriteMallIds(2L)).thenThrow(new RuntimeException("user-service down"));
        when(elasticsearchOperations.search(any(), eq(MallDocument.class))).thenReturn(mock(SearchHits.class));
        SearchResponse response = searchService.search(request);
        verify(userServiceClient).getFavoriteMallIds(2L);
        assertThat(response).isNotNull();
    }
} 