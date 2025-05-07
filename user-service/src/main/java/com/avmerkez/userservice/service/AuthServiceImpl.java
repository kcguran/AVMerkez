package com.avmerkez.userservice.service;

import com.avmerkez.userservice.repository.UserRepository;
import com.avmerkez.userservice.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    /*
    @Override
    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getFirstName(),
                registerRequest.getLastName());

        Set<Role> roles = new HashSet<>();
        // Default role is USER
        roles.add(Role.USER);
        user.setRoles(roles);

        userRepository.save(user);
        log.info("User registered successfully: {}", registerRequest.getUsername());
    }
    */
} 