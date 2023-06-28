package com.jensdev.common.infrastructureException;

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
