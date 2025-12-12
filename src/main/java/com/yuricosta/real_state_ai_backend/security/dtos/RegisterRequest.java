package com.yuricosta.real_state_ai_backend.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import org.hibernate.validator.constraints.Length;

public record RegisterRequest(
        @NotEmpty
        String name,
        @Email
        @NotEmpty
        String email,
        @Length(min = 6)
        @NotEmpty
        String password
) {
}
