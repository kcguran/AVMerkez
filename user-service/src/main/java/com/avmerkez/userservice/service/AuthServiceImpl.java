package com.avmerkez.userservice.service;

import com.avmerkez.userservice.dto.LoginRequest;
import com.avmerkez.userservice.dto.RegisterRequest;
import com.avmerkez.userservice.entity.Role;
import com.avmerkez.userservice.entity.User;
import com.avmerkez.userservice.exception.UserAlreadyExistsException;
import com.avmerkez.userservice.repository.UserRepository;
import com.avmerkez.userservice.security.JwtUtils;
import com.avmerkez.userservice.security.UserDetailsImpl;
import com.avmerkez.userservice.event.UserRegisteredEvent;
import com.avmerkez.userservice.entity.RefreshToken;
import com.avmerkez.userservice.exception.TokenRefreshException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public void registerUser(RegisterRequest registerRequest) throws UserAlreadyExistsException {
        // Kullanıcı adı veya e-posta var mı kontrol et
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("Bu kullanıcı adı zaten kullanılıyor!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Bu e-posta adresi zaten kullanılıyor!");
        }

        // Yeni kullanıcı nesnesi oluştur
        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getFirstName(),
                registerRequest.getLastName()
        );

        // Roller atanıyor
        Set<Role> roles = new HashSet<>();
        
        // Varsayılan olarak USER rolü verilir
        roles.add(Role.ROLE_USER);
        
        // Eğer rol belirtilmişse ve admin rolü varsa
        if (registerRequest.getRoles() != null) {
            registerRequest.getRoles().forEach(role -> {
                if ("admin".equalsIgnoreCase(role)) {
                    roles.add(Role.ROLE_ADMIN);
                } else if ("mall_manager".equalsIgnoreCase(role)) {
                    roles.add(Role.ROLE_MALL_MANAGER);
                }
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        
        logger.info("User registered successfully: {}", savedUser.getUsername());

        // Kullanıcı kayıt olayını yayınla
        eventPublisher.publishEvent(new UserRegisteredEvent(this, savedUser));
        logger.info("UserRegisteredEvent published for user: {}", savedUser.getUsername());
    }

    @Override
    public UserDetailsImpl authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // String jwt = jwtUtils.generateJwtToken(authentication); // JWT burada oluşturulmayacak, AuthController'da oluşturulacak cookie için.

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // List<String> roles = userDetails.getAuthorities().stream()
        //         .map(item -> item.getAuthority())
        //         .collect(Collectors.toList()); // Bu bilgi UserDetailsImpl içinde zaten var

        logger.info("User authenticated successfully: {}", userDetails.getUsername());

        // JwtResponse oluşturmak yerine doğrudan userDetails döndürülüyor.
        // AuthController bu userDetails'ı kullanarak hem cookie'yi oluşturacak hem de yanıt DTO'sunu hazırlayacak.
        return userDetails;
    }

    @Override
    @Transactional
    public UserDetailsImpl refreshAccessToken(String refreshTokenCookieValue) {
        if (refreshTokenCookieValue == null) {
            throw new TokenRefreshException("Refresh token cookie'de bulunamadı!");
        }
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenCookieValue)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new TokenRefreshException(refreshTokenCookieValue, "Refresh token veritabanında bulunamadı veya süresi dolmuş!"));

        User user = refreshToken.getUser();
        // Yeni bir JWT için UserDetailsImpl oluşturuluyor.
        // Bu UserDetailsImpl, AuthController tarafından yeni access token cookie'si oluşturmak için kullanılacak.
        // JWT'nin kendisi burada üretilmiyor, sadece UserDetailsImpl döndürülüyor.
        return UserDetailsImpl.build(user);
        // String newAccessToken = jwtUtils.generateTokenFromUsername(user.getUsername()); 
        // return new AccessTokenResponse(newAccessToken, "Bearer"); // Eski dönüş tipi
    }

    public void logout(String accessToken) {
        // Access token'ın jti'sini bul ve blocklist'e ekle
        Claims claims = Jwts.parserBuilder().setSigningKey(jwtUtils.key()).build().parseClaimsJws(accessToken).getBody();
        String jti = claims.getId();
        Date expiration = claims.getExpiration();
        if (jti != null && expiration != null) {
            long millis = expiration.getTime() - System.currentTimeMillis();
            refreshTokenService.blockAccessToken(jti, millis);
        }
        // Refresh token'ı da DB'den sil veya revoke et
        // ... mevcut logout işlemleri ...
    }
} 