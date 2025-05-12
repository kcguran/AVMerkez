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
        // In a microservice context that only validates JWTs and doesn't manage users,
        // the primary role of this service is to provide a UserDetails object
        // that AuthTokenFilter can use. The roles should ideally be parsed from the JWT itself.
        // This implementation is a placeholder and might need adjustment based on how roles are propagated in JWT.
        
        // For now, we assume username is sufficient and roles will be extracted from the token by JwtUtils if needed
        // or handled by Spring Security's expression-based access control using authorities from the token.
        // This UserDetailsServiceImpl will primarily be used by AuthTokenFilter to get a UserDetails object.
        // The actual roles for authorization will come from the token.
        logger.debug("Loading user by username (from JWT): {}", username);

        // Here, we are not fetching from DB. We're creating a UserDetails object.
        // Roles should be extracted from the JWT by the filter or JwtUtils.
        // This is a simplified version. If roles are directly in the JWT claims, JwtUtils can extract them.
        // Then UserDetailsImpl.build can use those roles.
        // For this example, we'll assume roles are handled by the token's authorities directly in SecurityContext.
        // Or, we could modify JwtUtils to return roles and pass them here.

        // Let's assume for now AuthTokenFilter will populate authorities directly from token if available.
        // This service might just return a UserDetails with the username.
        // A more robust solution would be to have JwtUtils extract roles and pass them to UserDetailsImpl.build().
        // For instance, if roles are a claim like "roles": ["ROLE_USER", "ROLE_ADMIN"]

        // Simplified: Create UserDetails with username. Roles come from parsed token.
        // This approach assumes that the AuthTokenFilter correctly extracts authorities from the JWT.
        // No database lookup is performed here.
        // Roles will be populated by the AuthTokenFilter after validating the token.
        // This service is mainly a contract for Spring Security.

        // The roles should be extracted from the token within the AuthTokenFilter
        // and used to create the UsernamePasswordAuthenticationToken with proper authorities.
        // This service just needs to return a UserDetails object based on the username.
        return UserDetailsImpl.build(username, List.of("ROLE_USER")); // Dummy role, actual roles from JWT
    }

    // Method to build UserDetails from JWT content (username and roles)
    public UserDetails loadUserByUsernameAndRoles(String username, List<String> roles) {
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("Username cannot be null or empty");
        }
        if (roles == null || roles.isEmpty()) {
            logger.warn("No roles provided for user: {}, assigning default USER role.", username);
            // Assign a default role or handle as an error, depending on requirements
            // For now, let's allow it but it might indicate an issue with token generation/propagation
             return UserDetailsImpl.build(username, List.of("ROLE_USER")); 
        }
        logger.debug("Building UserDetails for user: {} with roles: {}", username, roles);
        return UserDetailsImpl.build(username, roles);
    }

     public UserDetails loadUserDetailsFromToken(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            // Assuming roles are stored as a claim in the JWT, e.g., "roles"
            // This part needs JwtUtils to be able to extract roles claim.
            // For simplicity, let's assume roles are not directly extracted here but handled by filter.
            // This is a conceptual method; actual role extraction needs robust implementation in JwtUtils.
            Claims claims = Jwts.parserBuilder().setSigningKey(jwtUtils.key()).build().parseClaimsJws(token).getBody();
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);
            if (roles == null) {
                 logger.warn("Roles claim not found in JWT for user {}, defaulting to ROLE_USER", username);
                 roles = List.of("ROLE_USER"); // Default role if not present
            }
            return UserDetailsImpl.build(username, roles);
        } else {
            logger.warn("Invalid JWT token, cannot load UserDetails.");
            throw new UsernameNotFoundException("Invalid JWT token, cannot load UserDetails.");
        }
    }

} 