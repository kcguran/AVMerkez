package com.avmerkez.reviewservice.controller;

import com.avmerkez.reviewservice.dto.*;
import com.avmerkez.reviewservice.security.UserDetailsImpl;
import com.avmerkez.reviewservice.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "AVM ve mağaza yorumları ile ilgili işlemler")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @PostMapping
    @Operation(summary = "Yeni yorum oluştur", description = "AVM veya mağaza için yeni bir yorum oluşturur")
    public ResponseEntity<ApiResponse<ReviewDto>> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        ReviewDto reviewDto = reviewService.createReview(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Yorum başarıyla oluşturuldu", reviewDto));
    }
    
    @GetMapping("/public/mall/{mallId}")
    @Operation(summary = "AVM yorumlarını getir", description = "Belirli bir AVM'ye ait onaylanmış yorumları listeler")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewDto>>> getReviewsByMallId(
            @PathVariable Long mallId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PagedResponse<ReviewDto> reviews = reviewService.getReviewsByMallId(mallId, page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
    
    @GetMapping("/public/store/{storeId}")
    @Operation(summary = "Mağaza yorumlarını getir", description = "Belirli bir mağazaya ait onaylanmış yorumları listeler")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewDto>>> getReviewsByStoreId(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PagedResponse<ReviewDto> reviews = reviewService.getReviewsByStoreId(storeId, page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
    
    @GetMapping("/user")
    @Operation(summary = "Kullanıcı yorumlarını getir", description = "Giriş yapmış kullanıcının yorumlarını listeler")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewDto>>> getCurrentUserReviews(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PagedResponse<ReviewDto> reviews = reviewService.getReviewsByUserId(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
    
    @GetMapping("/public/mall/{mallId}/latest")
    @Operation(summary = "AVM için en son yorumları getir", description = "Belirli bir AVM'ye ait en son 5 onaylanmış yorumu listeler")
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getLatestReviewsByMallId(@PathVariable Long mallId) {
        List<ReviewDto> reviews = reviewService.getLatestReviewsByMallId(mallId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
    
    @GetMapping("/public/store/{storeId}/latest")
    @Operation(summary = "Mağaza için en son yorumları getir", description = "Belirli bir mağazaya ait en son 5 onaylanmış yorumu listeler")
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getLatestReviewsByStoreId(@PathVariable Long storeId) {
        List<ReviewDto> reviews = reviewService.getLatestReviewsByStoreId(storeId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Yorum detaylarını getir", description = "Belirli bir yorumun detaylarını getirir")
    public ResponseEntity<ApiResponse<ReviewDto>> getReviewById(@PathVariable Long id) {
        ReviewDto review = reviewService.getReviewById(id);
        return ResponseEntity.ok(ApiResponse.success(review));
    }
    
    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Onay bekleyen yorumları getir", description = "Onay bekleyen yorumları listeler (Sadece Admin)")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewDto>>> getPendingReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PagedResponse<ReviewDto> reviews = reviewService.getPendingReviews(page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
    
    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Yorum durumunu güncelle", description = "Yorumun onay durumunu günceller (Sadece Admin)")
    public ResponseEntity<ApiResponse<ReviewDto>> updateReviewStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewStatusRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        ReviewDto updatedReview = reviewService.updateReviewStatus(id, request, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Yorum durumu güncellendi", updatedReview));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Yorumu sil", description = "Belirli bir yorumu siler (Kullanıcı kendi yorumlarını, Admin tüm yorumları silebilir)")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        // Authorization is done in the service layer
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success("Yorum başarıyla silindi", null));
    }
} 