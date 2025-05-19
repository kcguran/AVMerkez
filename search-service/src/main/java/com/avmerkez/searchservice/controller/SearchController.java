package com.avmerkez.searchservice.controller;

import com.avmerkez.searchservice.dto.SearchRequest;
import com.avmerkez.searchservice.dto.SearchResponse;
import com.avmerkez.searchservice.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Search", description = "Serbest metin arama API'ı")
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @Operation(
        summary = "AVM arama",
        description = "AVM'leri serbest metin, şehir, kategori ve isteğe bağlı olarak kullanıcının favorileriyle filtreleyerek arar.",
        parameters = {
            @Parameter(name = "onlyFavorites", description = "true ise sadece kullanıcının favori AVM'leri döner. userId ile birlikte kullanılmalı."),
            @Parameter(name = "userId", description = "Kullanıcı ID'si. onlyFavorites=true ise zorunlu.")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Başarılı arama sonucu"),
            @ApiResponse(responseCode = "503", description = "User-service erişilemezse favori AVM filtresi uygulanmaz, sonuçlar favorisiz döner.")
        }
    )
    @PostMapping
    public ResponseEntity<SearchResponse> search(@Valid @RequestBody SearchRequest request) {
        return ResponseEntity.ok(searchService.search(request));
    }
} 