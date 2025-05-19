package com.avmerkez.searchservice.service.impl;

import com.avmerkez.searchservice.dto.SearchRequest;
import com.avmerkez.searchservice.dto.SearchResponse;
import com.avmerkez.searchservice.service.SearchService;
import com.avmerkez.searchservice.model.MallDocument;
import com.avmerkez.searchservice.repository.MallSearchRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import feign.FeignException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {
    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);
    private final MallSearchRepository mallSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final com.avmerkez.searchservice.client.UserServiceClient userServiceClient;

    @Override
    public SearchResponse search(SearchRequest request) {
        // Temel query
        var boolQuery = QueryBuilders.boolQuery();
        if (request.getQuery() != null && !request.getQuery().isBlank()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(request.getQuery(), "name", "city", "district", "description"));
        }
        if (request.getCity() != null) {
            boolQuery.filter(QueryBuilders.termQuery("city.keyword", request.getCity()));
        }
        if (request.getCategory() != null) {
            boolQuery.filter(QueryBuilders.termQuery("category.keyword", request.getCategory()));
        }
        // Kullanıcı favorileri
        if (Boolean.TRUE.equals(request.getOnlyFavorites()) && request.getUserId() != null) {
            Set<Long> favoriteMallIds;
            try {
                favoriteMallIds = userServiceClient.getFavoriteMallIds(request.getUserId());
            } catch (FeignException e) {
                log.warn("UserService erişilemiyor, favori AVM filtresi uygulanmadı. userId={}: {}", request.getUserId(), e.getMessage());
                favoriteMallIds = Set.of();
            } catch (Exception e) {
                log.error("Favori AVM id'leri alınırken hata oluştu. userId={}", request.getUserId(), e);
                favoriteMallIds = Set.of();
            }
            if (!favoriteMallIds.isEmpty()) {
                boolQuery.filter(QueryBuilders.termsQuery("id", favoriteMallIds));
            } else {
                // Hiç favori yoksa sonuç dönmesin
                boolQuery.filter(QueryBuilders.termsQuery("id", -1));
            }
        }
        // Öneri (suggest)
        if (Boolean.TRUE.equals(request.getSuggest())) {
            // Popüler AVM önerisi için popülerlik alanı varsa ona göre sıralama eklenebilir
            // Şimdilik mock: ilk 5 AVM'yi getir
        }
        NativeSearchQuery query = new NativeSearchQuery(boolQuery);
        SearchHits<MallDocument> hits = elasticsearchOperations.search(query, MallDocument.class);
        var results = hits.getSearchHits().stream().map(SearchHit::getContent).toList();
        // Facet (şehir, kategori bazlı sayım)
        Map<String, Long> cityCounts = results.stream().collect(Collectors.groupingBy(MallDocument::getCity, Collectors.counting()));
        Map<String, Long> categoryCounts = results.stream().collect(Collectors.groupingBy(m -> m.getCategory() == null ? "" : m.getCategory(), Collectors.counting()));
        return new SearchResponse(
            results,
            hits.getTotalHits(),
            cityCounts,
            categoryCounts
        );
    }
} 