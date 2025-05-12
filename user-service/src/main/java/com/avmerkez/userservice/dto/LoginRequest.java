package com.avmerkez.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kullanıcı giriş isteği için data transfer objesi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    private String username; // Kullanıcı adı veya e-posta kullanılabilir

    @NotBlank(message = "Şifre boş olamaz")
    private String password;
} 