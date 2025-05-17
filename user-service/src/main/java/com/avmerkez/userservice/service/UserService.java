package com.avmerkez.userservice.service;

import com.avmerkez.userservice.dto.UserProfileDto;
import com.avmerkez.userservice.dto.UpdateUserRequest;
import com.avmerkez.userservice.dto.UpdatePasswordRequest;
import com.avmerkez.userservice.dto.UpdateUserRolesRequest;

import java.util.List;
import java.util.Set;

/**
 * Kullanıcı profil işlemleri için servis arayüzü
 */
public interface UserService {
    
    /**
     * Mevcut oturum açmış kullanıcının bilgilerini getirir
     */
    UserProfileDto getCurrentUserProfile();
    
    /**
     * ID'si verilen kullanıcının bilgilerini getirir
     */
    UserProfileDto getUserProfile(Long userId);
    
    /**
     * Tüm kullanıcıların listesini döner
     */
    List<UserProfileDto> getAllUsers();

    /**
     * Kullanıcıyı günceller
     */
    UserProfileDto updateUser(Long userId, UpdateUserRequest request);

    /**
     * Kullanıcıyı siler
     */
    void deleteUser(Long userId);

    /**
     * Kullanıcı şifresini günceller
     */
    void updatePassword(UpdatePasswordRequest request);

    /**
     * Kullanıcı rollerini günceller
     */
    UserProfileDto updateUserRoles(Long userId, UpdateUserRolesRequest request);

    /**
     * Kullanıcının favori AVM'sini ekler
     */
    void addFavoriteMall(Long userId, Long mallId);

    /**
     * Kullanıcının favori AVM'sini siler
     */
    void removeFavoriteMall(Long userId, Long mallId);

    /**
     * Kullanıcının favori AVM ID listesini döner
     */
    Set<Long> getFavoriteMalls(Long userId);

    /**
     * Kullanıcının favori mağazasını ekler
     */
    void addFavoriteStore(Long userId, Long storeId);

    /**
     * Kullanıcının favori mağazasını siler
     */
    void removeFavoriteStore(Long userId, Long storeId);

    /**
     * Kullanıcının favori mağaza ID listesini döner
     */
    Set<Long> getFavoriteStores(Long userId);
} 