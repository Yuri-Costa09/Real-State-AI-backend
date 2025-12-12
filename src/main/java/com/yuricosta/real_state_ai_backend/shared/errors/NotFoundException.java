package com.yuricosta.real_state_ai_backend.shared.errors;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
