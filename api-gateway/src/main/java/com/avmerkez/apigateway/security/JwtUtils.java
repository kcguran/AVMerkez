package com.avmerkez.apigateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
@Slf4j
public class JwtUtils {

    // This secret MUST be the same as the one used in user-service
    // It should be fetched securely from Config Server or environment variables
    @Value("${avmerkez.app.jwtSecret:fallbackSecretKeyLongerThan256BitsForHS256}") // Use the same property name
    private String jwtSecret;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key()).build()
                    .parseClaimsJws(token).getBody().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error extracting username from JWT: {}", e.getMessage());
            return null; // Or throw specific exception
        }
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
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (Exception e) { // Catch broader exceptions during validation
            log.error("Error validating JWT token: {}", e.getMessage());
        }

        return false;
    }

    // Optional: Method to extract claims if needed by the filter
    public Claims getAllClaimsFromToken(String token) {
         try {
            return Jwts.parserBuilder().setSigningKey(key()).build()
                    .parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error extracting claims from JWT: {}", e.getMessage());
            return null; // Or throw specific exception
        }
    }
} 