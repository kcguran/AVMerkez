package com.avmerkez.userservice.service;

import com.avmerkez.userservice.dto.UserProfileDto;
import com.avmerkez.userservice.dto.UpdateUserRequest;
import com.avmerkez.userservice.dto.UpdatePasswordRequest;
import com.avmerkez.userservice.dto.UpdateUserRolesRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

/**
 * Kullanıcı profil işlemleri için servis implementasyonu
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
     * Kullanıcıyı günceller
     */
    @Override
    @Transactional
    public UserProfileDto updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı. ID: " + userId));
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        userRepository.save(user);
        logger.info("User updated: {}", userId);
        return mapUserToDto(user);
    }

    /**
     * Kullanıcıyı siler
     */
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UsernameNotFoundException("Kullanıcı bulunamadı. ID: " + userId);
        }
        userRepository.deleteById(userId);
        logger.info("User deleted: {}", userId);
    }

    /**
     * Kullanıcı şifresini günceller
     */
    @Override
    @Transactional
    public void updatePassword(UpdatePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + userDetails.getUsername()));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mevcut şifre hatalı");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        logger.info("User password updated: {}", user.getId());
    }

    /**
     * Kullanıcı rollerini günceller
     */
    @Override
    @Transactional
    public UserProfileDto updateUserRoles(Long userId, UpdateUserRolesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı. ID: " + userId));
        Set<Role> newRoles = request.getRoles().stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
        user.setRoles(newRoles);
        userRepository.save(user);
        logger.info("User roles updated: {} -> {}", userId, newRoles);
        return mapUserToDto(user);
    }

    @Override
    @Transactional
    public void addFavoriteMall(Long userId, Long mallId) {
        if (mallId == null) throw new IllegalArgumentException("Mall ID null olamaz");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı. ID: " + userId));
        if (!user.getFavoriteMallIds().add(mallId)) {
            throw new IllegalArgumentException("Bu AVM zaten favorilerde");
        }
        userRepository.save(user);
        logger.info("Favorite mall added: userId={}, mallId={}", userId, mallId);
    }

    @Override
    @Transactional
    public void removeFavoriteMall(Long userId, Long mallId) {
        if (mallId == null) throw new IllegalArgumentException("Mall ID null olamaz");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı. ID: " + userId));
        if (!user.getFavoriteMallIds().remove(mallId)) {
            throw new IllegalArgumentException("Bu AVM favorilerde yok");
        }
        userRepository.save(user);
        logger.info("Favorite mall removed: userId={}, mallId={}", userId, mallId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getFavoriteMalls(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı. ID: " + userId));
        return new HashSet<>(user.getFavoriteMallIds());
    }

    @Override
    @Transactional
    public void addFavoriteStore(Long userId, Long storeId) {
        if (storeId == null) throw new IllegalArgumentException("Store ID null olamaz");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı. ID: " + userId));
        if (!user.getFavoriteStoreIds().add(storeId)) {
            throw new IllegalArgumentException("Bu mağaza zaten favorilerde");
        }
        userRepository.save(user);
        logger.info("Favorite store added: userId={}, storeId={}", userId, storeId);
    }

    @Override
    @Transactional
    public void removeFavoriteStore(Long userId, Long storeId) {
        if (storeId == null) throw new IllegalArgumentException("Store ID null olamaz");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı. ID: " + userId));
        if (!user.getFavoriteStoreIds().remove(storeId)) {
            throw new IllegalArgumentException("Bu mağaza favorilerde yok");
        }
        userRepository.save(user);
        logger.info("Favorite store removed: userId={}, storeId={}", userId, storeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getFavoriteStores(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı. ID: " + userId));
        return new HashSet<>(user.getFavoriteStoreIds());
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
                user.getCreatedAt(),
                user.getFavoriteMallIds(),
                user.getFavoriteStoreIds()
        );
    }
} 