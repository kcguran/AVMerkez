package com.avmerkez.userservice.repository;

import com.avmerkez.userservice.entity.RefreshToken;
import com.avmerkez.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    
    @Query("SELECT r FROM RefreshToken r WHERE r.user.id = :userId AND r.revoked = false")
    Optional<RefreshToken> findByUserId(@Param("userId") Long userId);
    
    int deleteByUser(User user);
    void deleteByExpiryDateLessThan(Instant now); // For scheduled cleanup
} 