package com.yuricosta.real_state_ai_backend.security.dtos;

public record LoginResponse(
        String token,
        Long expiresIn
) {
}
