package com.avmerkez.userservice.service;

import com.avmerkez.userservice.dto.UserProfileDto;

import java.util.List;

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
} 