package com.avmerkez.userservice.repository;

import com.avmerkez.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Kullanıcı veri erişim nesnesi
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Kullanıcı adına göre kullanıcıyı bulur
     */
    Optional<User> findByUsername(String username);
    
    /**
     * E-posta adresine göre kullanıcıyı bulur
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Kullanıcı adı mevcut mu kontrol eder
     */
    boolean existsByUsername(String username);
    
    /**
     * E-posta adresi mevcut mu kontrol eder
     */
    boolean existsByEmail(String email);
} 