package com.avmerkez.campaigneventservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateCampaignRequest {

    @NotBlank(message = "Campaign name cannot be blank")
    @Size(max = 255, message = "Campaign name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDateTime startDate;

    @NotNull(message = "End date cannot be null")
    // TODO: Add custom validator to ensure end date is after start date
    private LocalDateTime endDate;

    // At least one of mallId, storeId, or brandId should ideally be present, 
    // but making them optional based on PRD. Service layer can add validation.
    private Long mallId;
    private Long storeId;
    private Long brandId;

    @Size(max = 100, message = "Discount type cannot exceed 100 characters")
    private String discountType;

    @Size(max = 500, message = "Conditions cannot exceed 500 characters")
    private String conditions;

    @Size(max = 50, message = "Campaign code cannot exceed 50 characters")
    private String campaignCode;
} 