package com.avmerkez.mallservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Mall details")
public class MallDto {
    @Schema(description = "Unique identifier of the mall", example = "1")
    private Long id;

    @Schema(description = "Name of the mall", example = "City Center Mall")
    private String name;

    @Schema(description = "City where the mall is located", example = "Ankara")
    private String city;

    @Schema(description = "District where the mall is located", example = "Çankaya")
    private String district;

    @Schema(description = "Full address of the mall", example = "Atatürk Blv. No:1, Kızılay", nullable = true)
    private String address;

    @Schema(description = "Latitude of the mall's location", example = "39.92077", nullable = true)
    private Double latitude;

    @Schema(description = "Longitude of the mall's location", example = "32.85411", nullable = true)
    private Double longitude;

    @Schema(description = "Working hours of the mall", example = "10:00 - 22:00", nullable = true)
    private String workingHours;

    @Schema(description = "Official website of the mall", example = "http://citycenter.com.tr", nullable = true)
    private String website;

    @Schema(description = "Contact phone number for the mall", example = "+90 312 123 4567", nullable = true)
    private String phoneNumber;

    // İhtiyaç duyulan diğer alanlar eklenebilir (adres vb.)
} 