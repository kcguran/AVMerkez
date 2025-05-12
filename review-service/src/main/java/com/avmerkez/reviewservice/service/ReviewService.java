package com.avmerkez.reviewservice.service;

import com.avmerkez.reviewservice.dto.CreateReviewRequest;
import com.avmerkez.reviewservice.dto.PagedResponse;
import com.avmerkez.reviewservice.dto.ReviewDto;
import com.avmerkez.reviewservice.dto.UpdateReviewStatusRequest;

import java.util.List;

public interface ReviewService {
    
    ReviewDto createReview(CreateReviewRequest request, Long userId);
    
    ReviewDto getReviewById(Long id);
    
    PagedResponse<ReviewDto> getReviewsByMallId(Long mallId, int page, int size);
    
    PagedResponse<ReviewDto> getReviewsByStoreId(Long storeId, int page, int size);
    
    PagedResponse<ReviewDto> getReviewsByUserId(Long userId, int page, int size);
    
    PagedResponse<ReviewDto> getPendingReviews(int page, int size);
    
    List<ReviewDto> getLatestReviewsByMallId(Long mallId);
    
    List<ReviewDto> getLatestReviewsByStoreId(Long storeId);
    
    ReviewDto updateReviewStatus(Long id, UpdateReviewStatusRequest request, Long adminId);
    
    void deleteReview(Long id);
} 