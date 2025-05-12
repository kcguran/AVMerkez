package com.avmerkez.reviewservice.dto;

import com.avmerkez.reviewservice.entity.ApprovalStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "AVM veya mağaza için yorum bilgisi")
public class ReviewDto {
    
    @Schema(description = "Yorumun ID'si")
    private Long id;
    
    @Schema(description = "Yorumu yapan kullanıcının ID'si")
    private Long userId;
    
    @Schema(description = "Yorum yapılan AVM'nin ID'si (eğer varsa)")
    private Long mallId;
    
    @Schema(description = "Yorum yapılan mağazanın ID'si (eğer varsa)")
    private Long storeId;
    
    @Schema(description = "Verilen puan (1-5 arası)")
    private Integer rating;
    
    @Schema(description = "Yazılan yorum metni")
    private String comment;
    
    @Schema(description = "Yorumun oluşturulma tarihi")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;
    
    @Schema(description = "Yorumun son güncelleme tarihi")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant updatedAt;
    
    @Schema(description = "Yorumun onay durumu (PENDING, APPROVED, REJECTED)")
    private ApprovalStatus approvalStatus;
    
    @Schema(description = "Reddedilme nedeni (eğer reddedildiyse)")
    private String rejectionReason;
} 