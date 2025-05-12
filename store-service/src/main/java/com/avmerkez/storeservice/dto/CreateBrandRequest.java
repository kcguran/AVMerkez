package com.avmerkez.storeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating a new brand")
public class CreateBrandRequest {
    @NotBlank(message = "Brand name cannot be blank")
    @Size(min = 1, max = 100, message = "Brand name must be between 1 and 100 characters")
    @Schema(description = "Name of the new brand", example = "Samsung", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 255, message = "Logo URL cannot exceed 255 characters")
    @Schema(description = "URL of the brand's logo", example = "https://cdn.example.com/samsung_logo.png", nullable = true)
    private String logoUrl;

    @Size(max = 255, message = "Website URL cannot exceed 255 characters")
    @Schema(description = "Official website of the brand", example = "https://www.samsung.com", nullable = true)
    private String websiteUrl;
} 