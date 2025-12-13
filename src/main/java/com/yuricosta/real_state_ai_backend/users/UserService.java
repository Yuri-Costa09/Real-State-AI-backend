package com.yuricosta.real_state_ai_backend.users;

import com.yuricosta.real_state_ai_backend.shared.errors.NotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findById(UUID uuid) {
        return userRepository.findById(uuid).orElseThrow(
                () -> new NotFoundException("Usuário não encontrado."));
    }

    public User saveUser(String name, String email, String password) {
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        User newUser = new User(name, email, encodedPassword);

        return userRepository.save(newUser);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
