package com.avmerkez.userservice.service;

import com.avmerkez.userservice.dto.UserProfileDto;
import com.avmerkez.userservice.entity.Role;
import com.avmerkez.userservice.entity.User;
import com.avmerkez.userservice.repository.UserRepository;
import com.avmerkez.userservice.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Kullanıcı profil işlemleri için servis implementasyonu
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Override
    public UserProfileDto getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + userDetails.getUsername()));
        
        logger.info("User profile fetched for: {}", userDetails.getUsername());
        return mapUserToDto(user);
    }

    @Override
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı. ID: " + userId));
        
        logger.info("User profile fetched by ID: {}", userId);
        return mapUserToDto(user);
    }

    @Override
    public List<UserProfileDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        logger.info("All users fetched, count: {}", users.size());
        return users.stream()
                .map(this::mapUserToDto)
                .collect(Collectors.toList());
    }

    /**
     * User entity'sini UserProfileDto'ya dönüştürür
     */
    private UserProfileDto mapUserToDto(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toList());
        
        return new UserProfileDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles,
                user.getCreatedAt()
        );
    }
} 