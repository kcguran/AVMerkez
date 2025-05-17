package com.avmerkez.campaigneventservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CampaignDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long mallId;
    private Long storeId;
    private Long brandId;
    private String discountType;
    private String conditions;
    private String campaignCode;
} 