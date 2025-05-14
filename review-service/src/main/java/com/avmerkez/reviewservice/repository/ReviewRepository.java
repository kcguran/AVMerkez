package com.avmerkez.reviewservice.repository;

import com.avmerkez.reviewservice.entity.ApprovalStatus;
import com.avmerkez.reviewservice.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByMallIdAndApprovalStatus(Long mallId, ApprovalStatus approvalStatus, Pageable pageable);
    Page<Review> findByStoreIdAndApprovalStatus(Long storeId, ApprovalStatus approvalStatus, Pageable pageable);
    Page<Review> findByUserIdAndApprovalStatus(Long userId, ApprovalStatus approvalStatus, Pageable pageable);
    Page<Review> findByUserId(Long userId, Pageable pageable);
    Page<Review> findByApprovalStatus(ApprovalStatus approvalStatus, Pageable pageable);
    List<Review> findTop5ByMallIdAndApprovalStatusOrderByCreatedAtDesc(Long mallId, ApprovalStatus approvalStatus);
    List<Review> findTop5ByStoreIdAndApprovalStatusOrderByCreatedAtDesc(Long storeId, ApprovalStatus approvalStatus);
} 