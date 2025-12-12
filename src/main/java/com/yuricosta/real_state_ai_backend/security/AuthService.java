package com.yuricosta.real_state_ai_backend.security;

import com.yuricosta.real_state_ai_backend.security.dtos.LoginResponse;
import com.yuricosta.real_state_ai_backend.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AuthService {

    private final JwtEncoder jwtEncoder;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public Jwt login(String email, String password) {
        var user = userRepository.findByEmail(email);

        if (user.isEmpty() || !bCryptPasswordEncoder.matches(password, user.get().getPassword())) {
            throw new BadCredentialsException("Email ou senha inválidos.");
        }

        var expiresIn = 3600L; // 1 hora

        var claims = JwtClaimsSet.builder()
                .subject(user.get().getId().toString())
                .claim("email", user.get().getEmail())
                .claim("name", user.get().getName())
                .claim("roles", user.get().getRoles())
                .issuer("real-state-ai-backend")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(expiresIn))
                .build();

        var token = jwtEncoder.encode(JwtEncoderParameters.from(claims));

        return token;
    }
}
