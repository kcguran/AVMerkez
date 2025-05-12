package com.avmerkez.apigateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;

import javax.crypto.SecretKey;
import java.util.List;

@Component
public class JwtUtilGateway {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtilGateway.class);

    @Value("${avmerkez.app.jwtSecret}") // This should be the SAME secret as user-service
    private String jwtSecret;

    @Value("${avmerkez.app.jwtCookieName:avm_jwt}")
    private String jwtCookieName;

    @Value("${avmerkez.app.jwtIssuer:avmerkez-user-service}")
    private String jwtIssuer;

    @Value("${avmerkez.app.jwtAudience:avmerkez-app}")
    private String jwtAudience;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public boolean validateToken(final String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();

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
        return false;
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        return getAllClaimsFromToken(token).get("roles", List.class);
    }

    public String getJwtFromCookies(ServerHttpRequest request) {
        HttpCookie cookie = request.getCookies().getFirst(jwtCookieName);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }
} 