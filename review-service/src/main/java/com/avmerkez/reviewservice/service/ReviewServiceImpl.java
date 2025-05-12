package com.avmerkez.reviewservice.service;

import com.avmerkez.reviewservice.client.MallServiceClient;
import com.avmerkez.reviewservice.client.StoreServiceClient;
import com.avmerkez.reviewservice.dto.CreateReviewRequest;
import com.avmerkez.reviewservice.dto.PagedResponse;
import com.avmerkez.reviewservice.dto.ReviewDto;
import com.avmerkez.reviewservice.dto.UpdateReviewStatusRequest;
import com.avmerkez.reviewservice.entity.ApprovalStatus;
import com.avmerkez.reviewservice.entity.Review;
import com.avmerkez.reviewservice.exception.InvalidInputException;
import com.avmerkez.reviewservice.exception.ResourceNotFoundException;
import com.avmerkez.reviewservice.mapper.ReviewMapper;
import com.avmerkez.reviewservice.repository.ReviewRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final MallServiceClient mallServiceClient;
    private final StoreServiceClient storeServiceClient;
    
    @Override
    public ReviewDto createReview(CreateReviewRequest request, Long userId) {
        validateTargetExistence(request.getMallId(), request.getStoreId());
        
        Review review = reviewMapper.toEntity(request, userId);
        Review savedReview = reviewRepository.save(review);
        log.info("Review created successfully with id: {} by user: {}", savedReview.getId(), userId);
        return reviewMapper.toDto(savedReview);
    }
    
    private void validateTargetExistence(Long mallId, Long storeId) {
        if (mallId != null) {
            try {
                log.debug("Checking existence of mallId: {}", mallId);
                mallServiceClient.checkMallExists(mallId);
                log.info("Mall check successful for mallId: {}.", mallId);
            } catch (FeignException.NotFound e) {
                log.warn("Mall validation failed. Mall with id {} not found.", mallId, e);
                throw new InvalidInputException("Geçersiz AVM ID: " + mallId + ". AVM bulunamadı.");
            } catch (FeignException e) {
                log.error("Error during Feign call to mall-service for mallId: {}. Status: {}, Message: {}", 
                          mallId, e.status(), e.getMessage(), e);
                throw new RuntimeException("AVM servisi ile iletişim kurulamadı.", e);
            }
        } else if (storeId != null) {
            try {
                log.debug("Checking existence of storeId: {}", storeId);
                storeServiceClient.checkStoreExists(storeId);
                log.info("Store check successful for storeId: {}.", storeId);
            } catch (FeignException.NotFound e) {
                log.warn("Store validation failed. Store with id {} not found.", storeId, e);
                throw new InvalidInputException("Geçersiz Mağaza ID: " + storeId + ". Mağaza bulunamadı.");
            } catch (FeignException e) {
                log.error("Error during Feign call to store-service for storeId: {}. Status: {}, Message: {}", 
                          storeId, e.status(), e.getMessage(), e);
                throw new RuntimeException("Mağaza servisi ile iletişim kurulamadı.", e);
            }
        } else {
            log.error("Validation error: Both mallId and storeId are null in createReview request.");
            throw new InvalidInputException("Yorum için AVM veya Mağaza ID belirtilmelidir.");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ReviewDto getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yorum", "id", id));
        return reviewMapper.toDto(review);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewDto> getReviewsByMallId(Long mallId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findByMallIdAndApprovalStatus(
                mallId, ApprovalStatus.APPROVED, pageable);
        return createPagedResponse(reviews);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewDto> getReviewsByStoreId(Long storeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findByStoreIdAndApprovalStatus(
                storeId, ApprovalStatus.APPROVED, pageable);
        return createPagedResponse(reviews);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewDto> getReviewsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findByUserIdAndApprovalStatus(
                userId, ApprovalStatus.APPROVED, pageable);
        return createPagedResponse(reviews);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewDto> getPendingReviews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Review> reviews = reviewRepository.findByApprovalStatus(ApprovalStatus.PENDING, pageable);
        return createPagedResponse(reviews);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> getLatestReviewsByMallId(Long mallId) {
        List<Review> reviews = reviewRepository.findTop5ByMallIdAndApprovalStatusOrderByCreatedAtDesc(
                mallId, ApprovalStatus.APPROVED);
        return reviewMapper.toDto(reviews);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> getLatestReviewsByStoreId(Long storeId) {
        List<Review> reviews = reviewRepository.findTop5ByStoreIdAndApprovalStatusOrderByCreatedAtDesc(
                storeId, ApprovalStatus.APPROVED);
        return reviewMapper.toDto(reviews);
    }
    
    @Override
    public ReviewDto updateReviewStatus(Long id, UpdateReviewStatusRequest request, Long adminId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yorum", "id", id));
        
        review.setApprovalStatus(request.getApprovalStatus());
        
        if (request.getApprovalStatus() == ApprovalStatus.APPROVED) {
            review.setApprovedAt(Instant.now());
            review.setApprovedBy(adminId);
            review.setRejectionReason(null);
        } else if (request.getApprovalStatus() == ApprovalStatus.REJECTED) {
            review.setApprovedAt(Instant.now());
            review.setApprovedBy(adminId);
            review.setRejectionReason(request.getRejectionReason());
        }
        
        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.toDto(updatedReview);
    }
    
    @Override
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yorum", "id", id));
        reviewRepository.delete(review);
    }
    
    private PagedResponse<ReviewDto> createPagedResponse(Page<Review> reviews) {
        List<ReviewDto> content = reviewMapper.toDto(reviews.getContent());
        
        return PagedResponse.<ReviewDto>builder()
                .content(content)
                .pageNumber(reviews.getNumber())
                .pageSize(reviews.getSize())
                .totalElements(reviews.getTotalElements())
                .totalPages(reviews.getTotalPages())
                .last(reviews.isLast())
                .build();
    }
} 