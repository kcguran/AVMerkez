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
public class ReviewCreatedEventDto {
    private Long reviewId;
    private Long userId;
    private Long mallId;
    private Long storeId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
} 