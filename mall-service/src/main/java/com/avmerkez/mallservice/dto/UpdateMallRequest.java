package com.avmerkez.mallservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMallRequest {

    // Null bırakılan alanlar güncellenmez mantığıyla ilerlenebilir
    // veya @NotBlank eklenip tüm alanların gönderilmesi zorunlu kılınabilir.

    @Size(min = 2, max = 100, message = "Mall name must be between 2 and 100 characters")
    private String name;

    private String address;

    private String city;

    private String district;

    // Güncellenebilecek diğer alanlar
} 