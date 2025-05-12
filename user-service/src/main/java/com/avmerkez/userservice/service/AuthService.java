package com.avmerkez.userservice.service;

import com.avmerkez.userservice.dto.LoginRequest;
import com.avmerkez.userservice.dto.RegisterRequest;
import com.avmerkez.userservice.exception.UserAlreadyExistsException;
import com.avmerkez.userservice.security.UserDetailsImpl;

/**
 * Interface for authentication related operations.
 * Currently empty as initial register/login endpoints are removed.
 * Will be populated with future authentication methods.
 */
public interface AuthService {
    void registerUser(RegisterRequest registerRequest) throws UserAlreadyExistsException;
    UserDetailsImpl authenticateUser(LoginRequest loginRequest);
    UserDetailsImpl refreshAccessToken(String refreshTokenCookieValue);
} 