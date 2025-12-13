package com.yuricosta.real_state_ai_backend.security.controllers;

import com.yuricosta.real_state_ai_backend.security.AuthService;
import com.yuricosta.real_state_ai_backend.security.dtos.LoginRequest;
import com.yuricosta.real_state_ai_backend.security.dtos.LoginResponse;
import com.yuricosta.real_state_ai_backend.security.dtos.RegisterRequest;
import com.yuricosta.real_state_ai_backend.security.dtos.RegisterResponse;
import com.yuricosta.real_state_ai_backend.shared.ApiResponse;
import com.yuricosta.real_state_ai_backend.users.UserRepository;
import com.yuricosta.real_state_ai_backend.users.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("api/v1/auth")

public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthController(BCryptPasswordEncoder bCryptPasswordEncoder, AuthService authService, UserService userService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {
        var user = userService.findByEmail(request.email());

        if (user == null) {
            throw new BadCredentialsException("Email ou senha inválidos.");
        }

        var token = authService.login(request.email(), request.password());

        var response = new LoginResponse(
                token.getTokenValue(),
                token.getExpiresAt().getEpochSecond()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @RequestBody RegisterRequest request
    ) {
        if (userService.emailExists(request.email())) {
            throw new BadCredentialsException("Usuário já registrado com este e-mail.");
        }

        var savedUser = userService.saveUser(request.name(), request.email(), request.password());

        var response = new RegisterResponse(
                savedUser.getId().toString(),
                savedUser.getName(),
                savedUser.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, "Usuário registrado com sucesso.", response)
        );
    }
}
