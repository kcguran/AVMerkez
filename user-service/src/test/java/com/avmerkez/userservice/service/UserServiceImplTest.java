package com.avmerkez.userservice.service;

import com.avmerkez.userservice.dto.UserProfileDto;
import com.avmerkez.userservice.entity.Role;
import com.avmerkez.userservice.entity.User;
import com.avmerkez.userservice.repository.UserRepository;
import com.avmerkez.userservice.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        // Setup user
        testUser = new User("testuser", "test@example.com", "encodedPassword", "Test", "User");
        testUser.setId(1L);
        testUser.setRoles(Set.of(Role.ROLE_USER));
        testUser.setCreatedAt(LocalDateTime.now());

        // Setup UserDetailsImpl
        userDetails = new UserDetailsImpl(
                testUser.getId(),
                testUser.getUsername(),
                testUser.getEmail(),
                testUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_USER.name()))
        );
    }

    @Test
    @DisplayName("Get current user profile should return profile for authenticated user")
    void getCurrentUserProfile_AuthenticatedUser_ReturnsProfile() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // When
        UserProfileDto profile = userService.getCurrentUserProfile();

        // Then
        assertThat(profile).isNotNull();
        assertThat(profile.getId()).isEqualTo(testUser.getId());
        assertThat(profile.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(profile.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(profile.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(profile.getLastName()).isEqualTo(testUser.getLastName());
        assertThat(profile.getRoles()).contains(Role.ROLE_USER.name());

        verify(userRepository).findById(testUser.getId());
    }

    @Test
    @DisplayName("Get current user profile should throw exception when user not found")
    void getCurrentUserProfile_UserNotFound_ThrowsException() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userService.getCurrentUserProfile());
        verify(userRepository).findById(testUser.getId());
    }

    @Test
    @DisplayName("Get user profile by ID should return profile for existing user")
    void getUserProfile_ExistingUserId_ReturnsProfile() {
        // Given
        Long userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        UserProfileDto profile = userService.getUserProfile(userId);

        // Then
        assertThat(profile).isNotNull();
        assertThat(profile.getId()).isEqualTo(testUser.getId());
        assertThat(profile.getUsername()).isEqualTo(testUser.getUsername());
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Get user profile by ID should throw exception when user not found")
    void getUserProfile_NonExistingUserId_ThrowsException() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserProfile(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Get all users should return list of user profiles")
    void getAllUsers_ReturnsUserList() {
        // Given
        User user2 = new User("user2", "user2@example.com", "pass2", "First2", "Last2");
        user2.setId(2L);
        user2.setRoles(Set.of(Role.ROLE_USER));
        user2.setCreatedAt(LocalDateTime.now());

        List<User> users = List.of(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<UserProfileDto> profiles = userService.getAllUsers();

        // Then
        assertThat(profiles).hasSize(2);
        assertThat(profiles.get(0).getUsername()).isEqualTo(testUser.getUsername());
        assertThat(profiles.get(1).getUsername()).isEqualTo(user2.getUsername());
        verify(userRepository).findAll();
    }
} 