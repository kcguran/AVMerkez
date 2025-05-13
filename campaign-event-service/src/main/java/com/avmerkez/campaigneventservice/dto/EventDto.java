package com.avmerkez.campaigneventservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventDto {
    private Long id;
    private String name;
    private Long mallId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String locationDescription;
    private String category;
} 