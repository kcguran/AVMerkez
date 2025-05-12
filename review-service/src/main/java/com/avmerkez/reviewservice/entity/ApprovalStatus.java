package com.avmerkez.reviewservice.entity;

/**
 * Enum class to represent the approval status of a review.
 */
public enum ApprovalStatus {
    /**
     * Review is pending approval by an admin.
     */
    PENDING,
    
    /**
     * Review has been approved by an admin.
     */
    APPROVED,
    
    /**
     * Review has been rejected by an admin.
     */
    REJECTED
} 