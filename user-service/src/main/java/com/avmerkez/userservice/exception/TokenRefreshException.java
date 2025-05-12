package com.avmerkez.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // Refresh token sorunları genellikle 403 Forbidden ile yanıtlanır
public class TokenRefreshException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TokenRefreshException(String token, String message) {
        super(String.format("Token [%s] için hata: %s", token, message));
    }
    
    public TokenRefreshException(String message) {
        super(message);
    }
} 