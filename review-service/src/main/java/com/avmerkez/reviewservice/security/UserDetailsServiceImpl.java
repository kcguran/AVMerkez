package com.avmerkez.reviewservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final JwtUtils jwtUtils;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("Bu servis doğrudan kimlik doğrulama yapmaz, token üzerinden kullanıcı bilgisi alır.");
    }
    
    public UserDetails loadUserDetailsFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtUtils.getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            String username = claims.getSubject();
            String rolesStr = claims.get("roles", String.class);
            List<String> roles = rolesStr != null ? 
                    Arrays.asList(rolesStr.split(",")) : 
                    List.of("ROLE_USER");
            
            Long userId = claims.get("userId", Long.class);
            String email = claims.get("email", String.class);
            
            return UserDetailsImpl.build(userId, username, email, roles);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Token geçersiz: " + e.getMessage());
        }
    }
} 