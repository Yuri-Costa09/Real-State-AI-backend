package com.yuricosta.real_state_ai_backend.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import org.hibernate.validator.constraints.Length;

public record RegisterResponse(
        String id,
        @NotEmpty
        String name,
        @Email
        @NotEmpty
        String email
) {
}
