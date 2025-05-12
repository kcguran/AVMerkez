package com.avmerkez.userservice.controller;

import com.avmerkez.userservice.dto.*;
import com.avmerkez.userservice.entity.RefreshToken;
import com.avmerkez.userservice.exception.TokenRefreshException;
import com.avmerkez.userservice.exception.UserAlreadyExistsException;
import com.avmerkez.userservice.security.JwtUtils;
import com.avmerkez.userservice.security.UserDetailsImpl;
import com.avmerkez.userservice.service.AuthService;
import com.avmerkez.userservice.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Kullanıcı kimlik doğrulama işlemleri için API endpoints
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    /**
     * Yeni kullanıcı kaydı yapar
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            authService.registerUser(registerRequest);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new MessageResponse("Kullanıcı başarıyla kaydedildi!"));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Kayıt sırasında beklenmeyen bir hata oluştu."));
        }
    }

    /**
     * Kullanıcı girişi yaparak JWT token döner
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            UserDetailsImpl userDetails = authService.authenticateUser(loginRequest);
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
            
            // Refresh token oluştur
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            ResponseCookie refreshTokenCookie = jwtUtils.generateRefreshTokenCookie(refreshToken.getToken());

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            UserPrincipalInfo userInfo = new UserPrincipalInfo(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(userInfo);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Giriş başarısız. Lütfen kullanıcı adı ve şifrenizi kontrol edin."));
        }
    }

    /**
     * Refresh token ile yeni bir JWT Access token oluşturur
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getRefreshTokenFromCookies(request);
        
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Refresh token bulunamadı"));
        }

        try {
            String newAccessToken = refreshTokenService.refreshAccessToken(refreshToken);
            ResponseCookie jwtCookie = ResponseCookie.from(jwtUtils.getJwtCookieName(), newAccessToken)
                    .path("/api")
                    .maxAge(jwtUtils.getJwtExpirationMs() / 1000)
                    .httpOnly(true)
                    .secure(jwtUtils.isSecureCookie())
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(new MessageResponse("Token başarıyla yenilendi"));
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie refreshCookie = jwtUtils.getCleanRefreshTokenCookie();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new MessageResponse("Başarıyla çıkış yaptınız."));
    }

    // Future endpoints for user profile management etc. can be added here
} 