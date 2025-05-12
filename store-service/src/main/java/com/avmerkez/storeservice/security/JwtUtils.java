package com.avmerkez.storeservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

// Cookie için importlar
import jakarta.servlet.http.Cookie;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${avmerkez.app.jwtSecret}")
    private String jwtSecret;

    @Value("${avmerkez.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${avmerkez.app.jwtCookieName:avm_jwt}") // Cookie adı eklendi
    private String jwtCookieName;

    @Value("${avmerkez.app.jwtIssuer:avmerkez-user-service}") // Default value updated
    private String jwtIssuer;

    @Value("${avmerkez.app.jwtAudience:avmerkez-app}")
    private String jwtAudience;

    @Value("${spring.profiles.active:dev}") // Aktif profili okumak için
    private String activeProfile;

    public SecretKey key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setIssuer(jwtIssuer)
                .setAudience(jwtAudience)
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
            .setIssuer(jwtIssuer)
            .setAudience(jwtAudience)
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(key(), SignatureAlgorithm.HS512)
            .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken).getBody();

            if (!jwtIssuer.equals(claims.getIssuer())) {
                logger.error("JWT token issuer mismatch. Expected: {}, Actual: {}", jwtIssuer, claims.getIssuer());
                return false;
            }

            if (!jwtAudience.equals(claims.getAudience())) {
                logger.error("JWT token audience mismatch. Expected: {}, Actual: {}", jwtAudience, claims.getAudience());
                return false;
            }
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        // SignatureException is implicitly covered by MalformedJwtException or parser errors in newer jjwt versions

        return false;
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }
} 