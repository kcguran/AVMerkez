package com.avmerkez.storeservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private JwtUtils jwtUtils; // To parse roles from token

    // This UserDetailsService is simplified for microservices.
    // It doesn't load users from a database but constructs UserDetails
    // based on the username (extracted from JWT) and roles (also from JWT if available).
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UsernameNotFoundException("Direct username-based load is not supported. Use JWT-based authentication.");
    }

    // Method to build UserDetails from JWT content (username and roles)
    public UserDetails loadUserByUsernameAndRoles(String username, List<String> roles) {
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("Username cannot be null or empty");
        }
        if (roles == null || roles.isEmpty()) {
            throw new UsernameNotFoundException("No roles found in JWT for user: " + username);
        }
        logger.debug("Building UserDetails for user: {} with roles: {}", username, roles);
        return UserDetailsImpl.build(username, roles);
    }

    public UserDetails loadUserDetailsFromToken(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            Claims claims = Jwts.parserBuilder().setSigningKey(jwtUtils.key()).build().parseClaimsJws(token).getBody();
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);
            if (roles == null || roles.isEmpty()) {
                throw new UsernameNotFoundException("No roles found in JWT for user: " + username);
            }
            return UserDetailsImpl.build(username, roles);
        } else {
            logger.warn("Invalid JWT token, cannot load UserDetails.");
            throw new UsernameNotFoundException("Invalid JWT token, cannot load UserDetails.");
        }
    }

} 