package com.avmerkez.mallservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating a new mall")
public class CreateMallRequest {

    @NotBlank(message = "Mall name cannot be blank")
    @Size(min = 2, max = 100, message = "Mall name must be between 2 and 100 characters")
    @Schema(description = "Name of the new mall", example = "Capital Mall", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Address cannot be blank")
    @Schema(description = "Full address of the new mall", example = "Eskişehir Yolu No:200, Söğütözü", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    @NotBlank(message = "City cannot be blank")
    @Schema(description = "City where the new mall is located", example = "Ankara", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @NotBlank(message = "District cannot be blank")
    @Schema(description = "District where the new mall is located", example = "Çankaya", requiredMode = Schema.RequiredMode.REQUIRED)
    private String district;

    @Schema(description = "Latitude of the mall's location", example = "39.9083", nullable = true)
    private Double latitude;

    @Schema(description = "Longitude of the mall's location", example = "32.7665", nullable = true)
    private Double longitude;

    @Size(max = 100)
    @Schema(description = "Working hours of the mall", example = "10:00 - 22:00", nullable = true)
    private String workingHours;

    @Size(max = 255)
    @Schema(description = "Official website of the mall", example = "http://capitalmall.com.tr", nullable = true)
    private String website;

    @Size(max = 20)
    @Schema(description = "Contact phone number for the mall", example = "+90 312 555 1122", nullable = true)
    private String phoneNumber;

    // Diğer zorunlu alanlar için validation eklenebilir
} 