package com.avmerkez.userservice.security;

import com.avmerkez.userservice.entity.User;
import com.avmerkez.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.avmerkez.userservice.security.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username or email: " + usernameOrEmail)));

        return UserDetailsImpl.build(user);
    }

    public UserDetails loadUserDetailsFromToken(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            Claims claims = Jwts.parserBuilder().setSigningKey(jwtUtils.key()).build().parseClaimsJws(token).getBody();
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);
            if (roles == null) {
                roles = List.of("ROLE_USER");
            }
            return UserDetailsImpl.build(new com.avmerkez.userservice.entity.User(username, null, null, null, null)); // Sadece username ile, roller authorities'den alınacak
        } else {
            throw new UsernameNotFoundException("Invalid JWT token, cannot load UserDetails.");
        }
    }
} 