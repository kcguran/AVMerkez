package com.avmerkez.reviewservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "mall_id")
    private Long mallId;

    @Column(name = "store_id")
    private Long storeId;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Size(min = 10, max = 1000)
    @Column(length = 1000)
    private String comment;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @NotNull
    @Column(name = "approval_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    /**
     * Check if both mallId and storeId are null or both are not null.
     * A review must be either for a mall or for a store, not both or neither.
     */
    @AssertTrue(message = "A review must be either for a mall or for a store, not both or neither")
    private boolean isEitherMallOrStoreIdSet() {
        return (mallId != null && storeId == null) || (mallId == null && storeId != null);
    }
} 