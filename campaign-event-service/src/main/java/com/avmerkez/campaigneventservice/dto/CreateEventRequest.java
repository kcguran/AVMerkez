package com.avmerkez.campaigneventservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
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
    // TODO: Add custom validator to ensure end date is after start date
    private LocalDateTime endDate;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @Size(max = 255, message = "Location description cannot exceed 255 characters")
    private String locationDescription;

    @Size(max = 100, message = "Category cannot exceed 100 characters")
    private String category;
} 