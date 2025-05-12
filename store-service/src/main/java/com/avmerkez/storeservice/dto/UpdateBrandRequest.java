package com.avmerkez.storeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for updating an existing brand")
public class UpdateBrandRequest {
    @Size(min = 1, max = 100, message = "Brand name must be between 1 and 100 characters")
    @Schema(description = "New name of the brand. If not provided, current name will be kept.", example = "LG Electronics", nullable = true)
    private String name;

    @Size(max = 255, message = "Logo URL cannot exceed 255 characters")
    @Schema(description = "New URL of the brand's logo. Provide an empty string to remove. If not provided, current logo URL will be kept.", example = "https://cdn.example.com/lg_logo.png", nullable = true)
    private String logoUrl;

    @Size(max = 255, message = "Website URL cannot exceed 255 characters")
    @Schema(description = "New official website of the brand. Provide an empty string to remove. If not provided, current website URL will be kept.", example = "https://www.lg.com", nullable = true)
    private String websiteUrl;
} 