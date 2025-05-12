package com.avmerkez.userservice.service;

import com.avmerkez.userservice.entity.RefreshToken;
import com.avmerkez.userservice.entity.User;
import com.avmerkez.userservice.exception.TokenRefreshException;
import com.avmerkez.userservice.repository.RefreshTokenRepository;
import com.avmerkez.userservice.repository.UserRepository;
import com.avmerkez.userservice.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${avmerkez.app.jwtRefreshExpirationMs:86400000}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));

        // Varolan refresh token'ı bul ve iptal et
        refreshTokenRepository.findByUserId(userId).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public String refreshAccessToken(String token) {
        return findByToken(token)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> jwtUtils.generateTokenFromUsername(user.getUsername()))
                .orElseThrow(() -> new TokenRefreshException(token, "Refresh token bulunamadı!"));
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isRevoked()) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token iptal edilmiş!");
        }

        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token süresi dolmuş!");
        }

        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.findByUserId(userId).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }
} 