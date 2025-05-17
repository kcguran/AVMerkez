package com.avmerkez.campaigneventservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.avmerkez.campaigneventservice.validator.DateRangeValid;

import java.time.LocalDateTime;

@Data
@DateRangeValid(start = "startDate", end = "endDate", message = "End date must be after start date")
public class CreateEventRequest {

    @NotBlank(message = "Event name cannot be blank")
    @Size(max = 255, message = "Event name cannot exceed 255 characters")
    private String name;

    @NotNull(message = "Mall ID cannot be null for an event")
    private Long mallId;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDateTime startDate;

    @NotNull(message = "End date cannot be null")
    private LocalDateTime endDate;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @Size(max = 255, message = "Location description cannot exceed 255 characters")
    private String locationDescription;

    @Size(max = 100, message = "Category cannot exceed 100 characters")
    private String category;
} 