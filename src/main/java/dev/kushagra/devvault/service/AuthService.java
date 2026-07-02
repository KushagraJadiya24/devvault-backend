package dev.kushagra.devvault.service;

import dev.kushagra.devvault.dto.LoginRequest;
import dev.kushagra.devvault.dto.RegisterRequest;
import dev.kushagra.devvault.model.Role;
import dev.kushagra.devvault.model.User;
import dev.kushagra.devvault.repository.UserRepository;
import dev.kushagra.devvault.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.MEMBER);

        userRepository.save(user);

        return jwtUtil.generateToken(user.getId(), user.getRole().name());
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getId(), user.getRole().name());
    }
}