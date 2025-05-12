package com.avmerkez.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Kullanıcı kayıt isteği için data transfer objesi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    @Size(min = 3, max = 50, message = "Kullanıcı adı 3-50 karakter arasında olmalıdır")
    private String username;

    @NotBlank(message = "E-posta boş olamaz")
    @Size(max = 100, message = "E-posta en fazla 100 karakter olabilir")
    @Email(message = "Geçerli bir e-posta adresi girilmelidir")
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 6, max = 40, message = "Şifre 6-40 karakter arasında olmalıdır")
    private String password;

    @Size(max = 50, message = "Ad en fazla 50 karakter olabilir")
    private String firstName;

    @Size(max = 50, message = "Soyad en fazla 50 karakter olabilir")
    private String lastName;

    private Set<String> roles; // ROLE_USER, ROLE_ADMIN, ROLE_MALL_MANAGER vs.
} 