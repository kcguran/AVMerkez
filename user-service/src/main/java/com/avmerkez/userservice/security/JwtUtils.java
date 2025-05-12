package com.avmerkez.userservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.WebUtils;
import jakarta.servlet.http.Cookie;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtUtils {

    @Value("${avmerkez.app.jwtSecret:fallbackSecretKeyLongerThan256BitsForHS256}") // Fetch from properties or use fallback
    private String jwtSecret;

    @Value("${avmerkez.app.jwtExpirationMs:86400000}") // Default to 1 day
    private int jwtExpirationMs;

    @Value("${avmerkez.app.jwtCookieName:avm_jwt}")
    private String jwtCookie;

    @Value("${avmerkez.app.jwtRefreshCookieName:avm_refresh_token}") // Refresh token cookie adı
    private String jwtRefreshCookieName;

    @Value("${avmerkez.app.jwtRefreshExpirationMs:604800000}") // Refresh token geçerlilik süresi
    private int jwtRefreshExpirationMs;

    @Value("${avmerkez.app.jwtIssuer:avmerkez-user-service}") // Eklendi
    private String jwtIssuer;

    @Value("${avmerkez.app.jwtAudience:avmerkez-api}") // Eklendi
    private String jwtAudience;

    @Value("${spring.profiles.active:dev}") // Aktif profili oku, varsayılan 'dev'
    private String activeProfile;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer(jwtIssuer) // iss claim eklendi
                .setAudience(jwtAudience) // aud claim eklendi
                .setId(UUID.randomUUID().toString()) // jti claim eklendi
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256) // HS256 kullandığımızı varsayıyorum, user-service için bu şekildeydi.
                .compact();
    }

    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        return ResponseCookie.from(jwtCookie, jwt)
                .path("/api")
                .maxAge(jwtExpirationMs / 1000)
                .httpOnly(true)
                .secure("prod".equalsIgnoreCase(activeProfile)) // Profile göre secure ayarı
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie generateRefreshTokenCookie(String refreshTokenString) {
        return ResponseCookie.from(jwtRefreshCookieName, refreshTokenString)
                .path("/api/v1/auth/refresh-token") // Sadece refresh endpoint'i için geçerli path
                .maxAge(jwtRefreshExpirationMs / 1000)
                .httpOnly(true)
                .secure("prod".equalsIgnoreCase(activeProfile)) // Profile göre secure ayarı
                .sameSite("Lax") // veya "Strict"
                .build();
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null)
                .path("/api")
                .maxAge(0)
                .httpOnly(true)
                .secure("prod".equalsIgnoreCase(activeProfile)) // Profile göre secure ayarı
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie getCleanRefreshTokenCookie() {
        return ResponseCookie.from(jwtRefreshCookieName, null)
                .path("/api/v1/auth/refresh-token")
                .maxAge(0)
                .httpOnly(true)
                .secure("prod".equalsIgnoreCase(activeProfile)) // Profile göre secure ayarı
                .sameSite("Lax")
                .build();
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtRefreshCookieName);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                   .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error validating JWT token: {}", e.getMessage());
        }
        return false;
    }

    public String getJwtCookieName() {
        return jwtCookie;
    }

    public int getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public boolean isSecureCookie() {
        return "prod".equalsIgnoreCase(activeProfile);
    }
} 