package com.avmerkez.userservice.controller;

import com.avmerkez.userservice.dto.UserProfileDto;
import com.avmerkez.userservice.dto.UpdateUserRequest;
import com.avmerkez.userservice.dto.UpdatePasswordRequest;
import com.avmerkez.userservice.dto.UpdateUserRolesRequest;
import com.avmerkez.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

/**
 * Kullanıcı profil işlemleri için API endpoints
 */
@Tag(name = "User", description = "Kullanıcı işlemleri")
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

    /**
     * Kullanıcı profilini günceller (kendi profili veya admin başkasını)
     */
    @PutMapping("/{id}")
    @PreAuthorize("#id == principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserProfileDto> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        UserProfileDto updated = userService.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Kullanıcıyı siler (kendi profili veya admin başkasını)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("#id == principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Kullanıcı şifresini günceller (kendi profili)
     */
    @PostMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Kullanıcı rollerini günceller (sadece admin)
     */
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserProfileDto> updateUserRoles(@PathVariable Long id, @Valid @RequestBody UpdateUserRolesRequest request) {
        UserProfileDto updated = userService.updateUserRoles(id, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Favori AVM ekle", description = "Kullanıcının favori AVM listesine AVM ekler.")
    @PostMapping("/me/favorites/malls/{mallId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addFavoriteMall(@Parameter(description = "AVM ID", required = true) @PathVariable @NotNull Long mallId) {
        UserProfileDto user = userService.getCurrentUserProfile();
        userService.addFavoriteMall(user.getId(), mallId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Favori AVM sil", description = "Kullanıcının favori AVM listesinden AVM çıkarır.")
    @DeleteMapping("/me/favorites/malls/{mallId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFavoriteMall(@Parameter(description = "AVM ID", required = true) @PathVariable @NotNull Long mallId) {
        UserProfileDto user = userService.getCurrentUserProfile();
        userService.removeFavoriteMall(user.getId(), mallId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Favori AVM'leri listele", description = "Kullanıcının favori AVM ID listesini döner.")
    @GetMapping("/me/favorites/malls")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Set<Long>> getFavoriteMalls() {
        UserProfileDto user = userService.getCurrentUserProfile();
        return ResponseEntity.ok(userService.getFavoriteMalls(user.getId()));
    }

    @Operation(summary = "Favori mağaza ekle", description = "Kullanıcının favori mağaza listesine mağaza ekler.")
    @PostMapping("/me/favorites/stores/{storeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addFavoriteStore(@Parameter(description = "Mağaza ID", required = true) @PathVariable @NotNull Long storeId) {
        UserProfileDto user = userService.getCurrentUserProfile();
        userService.addFavoriteStore(user.getId(), storeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Favori mağaza sil", description = "Kullanıcının favori mağaza listesinden mağaza çıkarır.")
    @DeleteMapping("/me/favorites/stores/{storeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFavoriteStore(@Parameter(description = "Mağaza ID", required = true) @PathVariable @NotNull Long storeId) {
        UserProfileDto user = userService.getCurrentUserProfile();
        userService.removeFavoriteStore(user.getId(), storeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Favori mağazaları listele", description = "Kullanıcının favori mağaza ID listesini döner.")
    @GetMapping("/me/favorites/stores")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Set<Long>> getFavoriteStores() {
        UserProfileDto user = userService.getCurrentUserProfile();
        return ResponseEntity.ok(userService.getFavoriteStores(user.getId()));
    }
} 