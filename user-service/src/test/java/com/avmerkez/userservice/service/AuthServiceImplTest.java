package com.avmerkez.userservice.service;

import com.avmerkez.userservice.dto.LoginRequest;
import com.avmerkez.userservice.dto.RegisterRequest;
import com.avmerkez.userservice.entity.Role;
import com.avmerkez.userservice.entity.User;
import com.avmerkez.userservice.exception.UserAlreadyExistsException;
import com.avmerkez.userservice.repository.UserRepository;
import com.avmerkez.userservice.security.JwtUtils;
import com.avmerkez.userservice.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        // Setup register request
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");

        // Setup login request
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // Setup user
        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                "encodedPassword",
                registerRequest.getFirstName(),
                registerRequest.getLastName()
        );
        user.setId(1L);
        user.setRoles(Set.of(Role.ROLE_USER));

        // Setup UserDetailsImpl
        userDetails = new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_USER.name()))
        );
    }

    @Test
    @DisplayName("Register user should save user with encoded password")
    void registerUser_ValidData_UserSaved() {
        // Given
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

        // When
        authService.registerUser(registerRequest);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertThat(capturedUser.getUsername()).isEqualTo(registerRequest.getUsername());
        assertThat(capturedUser.getEmail()).isEqualTo(registerRequest.getEmail());
        assertThat(capturedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(capturedUser.getRoles()).contains(Role.ROLE_USER);
    }

    @Test
    @DisplayName("Register user with existing username should throw UserAlreadyExistsException")
    void registerUser_ExistingUsername_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Register user with existing email should throw UserAlreadyExistsException")
    void registerUser_ExistingEmail_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Authenticate user should return UserDetailsImpl")
    void authenticateUser_ValidCredentials_ReturnsUserDetails() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // When
        UserDetailsImpl result = authService.authenticateUser(loginRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(userDetails.getUsername());
        assertThat(result.getEmail()).isEqualTo(userDetails.getEmail());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
} 