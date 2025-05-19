package com.avmerkez.mallservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AVM hizmeti (facility) güncelleme isteği")
public class UpdateFacilityRequest {
    @NotBlank
    @Size(max = 100)
    @Schema(description = "Hizmet adı", example = "Danışma", required = true)
    private String name;

    @Size(max = 1000)
    @Schema(description = "Açıklama", example = "Zemin katta, danışma masası.", nullable = true)
    private String description;

    @Size(max = 50)
    @Schema(description = "Kat bilgisi", example = "Zemin Kat", nullable = true)
    private String floor;
} 