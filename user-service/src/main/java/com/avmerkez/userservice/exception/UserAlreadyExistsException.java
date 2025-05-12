package com.avmerkez.userservice.exception;

/**
 * Kullanıcı kayıt işleminde aynı username veya email ile kullanıcı varsa fırlatılır
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
} 