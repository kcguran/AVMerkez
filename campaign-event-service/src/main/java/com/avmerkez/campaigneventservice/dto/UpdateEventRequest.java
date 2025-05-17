package com.avmerkez.campaigneventservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.avmerkez.campaigneventservice.validator.DateRangeValid;

import java.time.LocalDateTime;

@Data
@DateRangeValid(start = "startDate", end = "endDate", message = "End date must be after start date")
public class UpdateEventRequest {

    @Size(max = 255, message = "Event name cannot exceed 255 characters")
    private String name;

    // Typically mallId shouldn't be updated, but allowed here if needed.
    // Re-validation would be necessary.
    private Long mallId;

    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDateTime startDate;

    // TODO: Add custom validator to ensure end date is after start date
    private LocalDateTime endDate;

    private String description;

    @Size(max = 255, message = "Location description cannot exceed 255 characters")
    private String locationDescription;

    @Size(max = 100, message = "Category cannot exceed 100 characters")
    private String category;
} 