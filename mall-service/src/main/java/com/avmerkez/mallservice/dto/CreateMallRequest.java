package com.avmerkez.mallservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMallRequest {

    @NotBlank(message = "Mall name cannot be blank")
    @Size(min = 2, max = 100, message = "Mall name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotBlank(message = "City cannot be blank")
    private String city;

    @NotBlank(message = "District cannot be blank")
    private String district;

    // Diğer zorunlu alanlar için validation eklenebilir
} 