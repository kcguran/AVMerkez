package com.avmerkez.mallservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for updating an existing mall. Only provided fields will be updated.")
public class UpdateMallRequest {

    // Null bırakılan alanlar güncellenmez mantığıyla ilerlenebilir
    // veya @NotBlank eklenip tüm alanların gönderilmesi zorunlu kılınabilir.

    @Size(min = 2, max = 100, message = "Mall name must be between 2 and 100 characters")
    @Schema(description = "New name of the mall. If not provided, current name will be kept.", example = "Capital Mall Deluxe", nullable = true)
    private String name;

    @Schema(description = "New full address of the mall. If not provided, current address will be kept.", example = "Eskişehir Yolu No:200, Söğütözü, Ankara", nullable = true)
    private String address;

    @Schema(description = "New city where the mall is located. If not provided, current city will be kept.", example = "Ankara", nullable = true)
    private String city;

    @Schema(description = "New district where the mall is located. If not provided, current district will be kept.", example = "Yenimahalle", nullable = true)
    private String district;

    @Schema(description = "New latitude of the mall's location. If not provided, current latitude will be kept.", example = "39.9085", nullable = true)
    private Double latitude;

    @Schema(description = "New longitude of the mall's location. If not provided, current longitude will be kept.", example = "32.7660", nullable = true)
    private Double longitude;

    @Size(max = 100)
    @Schema(description = "New working hours for the mall. If not provided, current working hours will be kept.", example = "09:00 - 23:00", nullable = true)
    private String workingHours;

    @Size(max = 255)
    @Schema(description = "New official website for the mall. If not provided, current website will be kept.", example = "http://newcapitalmall.com.tr", nullable = true)
    private String website;

    @Size(max = 20)
    @Schema(description = "New contact phone number for the mall. If not provided, current phone number will be kept.", example = "+90 312 555 1133", nullable = true)
    private String phoneNumber;

    // Güncellenebilecek diğer alanlar
} 