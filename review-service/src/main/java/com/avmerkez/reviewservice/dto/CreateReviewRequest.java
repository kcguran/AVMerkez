package com.avmerkez.reviewservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yeni yorum oluşturma isteği")
public class CreateReviewRequest {
    
    @Schema(description = "Yorum yapılan AVM'nin ID'si (storeId ile aynı anda verilmemeli)", example = "1")
    private Long mallId;
    
    @Schema(description = "Yorum yapılan mağazanın ID'si (mallId ile aynı anda verilmemeli)", example = "1")
    private Long storeId;
    
    @NotNull(message = "Puan verilmesi zorunludur")
    @Min(value = 1, message = "Puan en az 1 olmalıdır")
    @Max(value = 5, message = "Puan en fazla 5 olmalıdır")
    @Schema(description = "Verilen puan (1-5 arası)", example = "4", required = true)
    private Integer rating;
    
    @Size(min = 10, max = 1000, message = "Yorum en az 10, en fazla 1000 karakter olmalıdır")
    @Schema(description = "Yazılan yorum metni", example = "Bu AVM'de çok güzel mağazalar var ve otopark imkanı çok iyi.")
    private String comment;
    
    @AssertTrue(message = "AVM veya mağaza ID'si belirtilmelidir, ikisi birden veya hiçbiri belirtilmemelidir")
    private boolean isEitherMallOrStoreIdSet() {
        return (mallId != null && storeId == null) || (mallId == null && storeId != null);
    }
} 