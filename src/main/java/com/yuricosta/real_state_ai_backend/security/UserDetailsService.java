package com.yuricosta.real_state_ai_backend.security;

import com.yuricosta.real_state_ai_backend.user.User;
import com.yuricosta.real_state_ai_backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(()
                -> new UsernameNotFoundException("Usuário não encontrado."));
        return new UserDetailsImpl(user);
    }
}