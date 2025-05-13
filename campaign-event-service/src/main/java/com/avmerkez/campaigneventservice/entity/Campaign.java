package com.avmerkez.campaigneventservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
@NoArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @Lob // For potentially long descriptions
    @Column(nullable = false)
    private String description;

    @NotNull
    @FutureOrPresent
    @Column(nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime endDate;

    // Relationships - These IDs point to entities potentially in other microservices
    // We store the ID directly. Validation would happen via Feign calls in the service layer.
    private Long mallId; // Optional
    private Long storeId; // Optional
    private Long brandId; // Optional

    @Size(max = 100)
    private String discountType; // Or use an Enum if types are fixed (e.g., PERCENTAGE, FIXED_AMOUNT)

    @Size(max = 500)
    private String conditions;

    @Size(max = 50)
    private String campaignCode; // Optional

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    // Constraint: endDate must be after startDate (Consider adding a class-level validation annotation)
    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }
} 