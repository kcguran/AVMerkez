package com.avmerkez.notificationservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreatedEventDto {
    private Long eventId;
    private String name;
    private String description;
    private Long mallId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String locationDescription;
    private String category;
} 