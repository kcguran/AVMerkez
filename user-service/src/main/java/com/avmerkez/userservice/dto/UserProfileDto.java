package com.avmerkez.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Kullanıcı profil bilgilerini taşıyan DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    private LocalDateTime createdAt;
    
    // İleriki aşamalarda favoriler, yorumlar vs. eklenebilir
} 