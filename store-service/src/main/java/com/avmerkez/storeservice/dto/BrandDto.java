package com.avmerkez.storeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Brand details")
public class BrandDto {
    @Schema(description = "Unique identifier of the brand", example = "101")
    private Long id;

    @Schema(description = "Name of the brand", example = "Sony")
    private String name;

    @Schema(description = "URL of the brand's logo", example = "https://cdn.example.com/sony_logo.png", nullable = true)
    private String logoUrl;

    @Schema(description = "Official website of the brand", example = "https://www.sony.com", nullable = true)
    private String websiteUrl;
} 