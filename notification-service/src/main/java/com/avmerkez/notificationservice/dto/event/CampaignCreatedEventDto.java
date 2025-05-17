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
public class CampaignCreatedEventDto {
    private Long campaignId;
    private String name;
    private String description;
    private Long mallId;
    private Long storeId;
    private Long brandId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
} 