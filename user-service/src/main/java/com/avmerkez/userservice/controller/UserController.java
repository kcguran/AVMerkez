package com.avmerkez.userservice.controller;

import com.avmerkez.userservice.dto.UserProfileDto;
import com.avmerkez.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Kullanıcı profil işlemleri için API endpoints
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Oturum açan kullanıcının profil bilgilerini getirir
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile() {
        UserProfileDto userProfile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(userProfile);
    }

    /**
     * Kullanıcı ID'sine göre profil bilgilerini getirir (sadece admin erişebilir)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long id) {
        UserProfileDto userProfile = userService.getUserProfile(id);
        return ResponseEntity.ok(userProfile);
    }

    /**
     * Tüm kullanıcıları listeler (sadece admin erişebilir)
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
} 