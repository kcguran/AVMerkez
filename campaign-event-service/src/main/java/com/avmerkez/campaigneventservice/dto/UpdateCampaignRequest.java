package com.avmerkez.campaigneventservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.avmerkez.campaigneventservice.validator.DateRangeValid;

import java.time.LocalDateTime;

@Data
@DateRangeValid(start = "startDate", end = "endDate", message = "End date must be after start date")
public class UpdateCampaignRequest {

    @Size(max = 255, message = "Campaign name cannot exceed 255 characters")
    private String name; // Optional update

    private String description; // Optional update

    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDateTime startDate; // Optional update

    // TODO: Add custom validator to ensure end date is after start date
    private LocalDateTime endDate; // Optional update

    // Allowing updates to these IDs might require re-validation
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