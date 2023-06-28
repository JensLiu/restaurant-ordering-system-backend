package com.jensdev.common.authException;

public class AuthException extends RuntimeException {
    private final String message;

    public AuthException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
