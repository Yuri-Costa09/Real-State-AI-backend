package com.yuricosta.real_state_ai_backend.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
        @Email
        @NotEmpty
        String email,
        @NotEmpty
        String password
) {
}
