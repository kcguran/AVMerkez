package com.avmerkez.mallservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AVM hizmeti (facility) DTO")
public class FacilityDto {
    @Schema(description = "Hizmetin ID'si", example = "1")
    private Long id;

    @Schema(description = "Hizmet adı", example = "Danışma")
    private String name;

    @Schema(description = "Açıklama", example = "Zemin katta, danışma masası.", nullable = true)
    private String description;

    @Schema(description = "Kat bilgisi", example = "Zemin Kat", nullable = true)
    private String floor;
} 