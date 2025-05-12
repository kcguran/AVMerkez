package com.avmerkez.reviewservice.mapper;

import com.avmerkez.reviewservice.dto.CreateReviewRequest;
import com.avmerkez.reviewservice.dto.ReviewDto;
import com.avmerkez.reviewservice.entity.ApprovalStatus;
import com.avmerkez.reviewservice.entity.Review;
import org.mapstruct.*;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring", 
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReviewMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "mallId", source = "request.mallId")
    @Mapping(target = "storeId", source = "request.storeId")
    @Mapping(target = "rating", source = "request.rating")
    @Mapping(target = "comment", source = "request.comment")
    @Mapping(target = "approvalStatus", constant = "PENDING")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    Review toEntity(CreateReviewRequest request, Long userId);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "mallId", source = "mallId")
    @Mapping(target = "storeId", source = "storeId")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "approvalStatus", source = "approvalStatus")
    @Mapping(target = "rejectionReason", source = "rejectionReason")
    ReviewDto toDto(Review review);
    
    List<ReviewDto> toDto(List<Review> reviews);
    
    @AfterMapping
    default void setDefaultValues(CreateReviewRequest request, @MappingTarget Review review) {
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(Instant.now());
        }
        if (review.getApprovalStatus() == null) {
            review.setApprovalStatus(ApprovalStatus.PENDING);
        }
    }
} 