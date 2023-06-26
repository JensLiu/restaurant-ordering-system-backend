package com.jensdev.common.exceptions;

public class InfrastructureException extends RuntimeException {
    private final String message;

    public InfrastructureException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
