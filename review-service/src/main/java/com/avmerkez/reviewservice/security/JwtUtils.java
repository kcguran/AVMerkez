package com.avmerkez.reviewservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.security.Key;

@Component
public class JwtUtils {
    
    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${avmerkez.app.jwtSecret:fallbackSecretKeyLongerThan256BitsForHS256}")
    private String jwtSecret;

    @Value("${avmerkez.app.jwtCookieName:avm_jwt}")
    private String jwtCookie;

    @Value("${avmerkez.app.jwtIssuer:avmerkez-user-service}")
    private String jwtIssuer;

    @Value("${avmerkez.app.jwtAudience:avmerkez-api}")
    private String jwtAudience;

    public Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    private Key key() {
        return getKey();
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(authToken);
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
} 