package com.avmerkez.reviewservice.dto;

import com.avmerkez.reviewservice.entity.ApprovalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yorum onay durumu güncelleme isteği")
public class UpdateReviewStatusRequest {
    
    @NotNull(message = "Onay durumu belirtilmelidir")
    @Schema(description = "Yeni onay durumu (APPROVED veya REJECTED)")
    private ApprovalStatus approvalStatus;
    
    @Size(max = 500, message = "Red nedeni en fazla 500 karakter olmalıdır")
    @Schema(description = "Reddetme nedeni (eğer REJECTED ise)", example = "Uygunsuz içerik")
    private String rejectionReason;
} 