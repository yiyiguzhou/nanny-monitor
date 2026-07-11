package com.example.nanny.controller;

import com.example.nanny.domain.User;
import com.example.nanny.repository.UserRepository;
import com.example.nanny.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String role = body.getOrDefault("role", "USER");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "username and password are required"));
        }

        User existing = userRepository.findByUsername(username);
        if (existing != null) {
            return ResponseEntity.badRequest().body(Map.of("error", "username already exists"));
        }

        if (!"ADMIN".equals(role) && !"USER".equals(role)) {
            return ResponseEntity.badRequest().body(Map.of("error", "role must be ADMIN or USER"));
        }

        User user = new User(username, passwordEncoder.encode(password), role);
        userRepository.insert(user);

        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

        return ResponseEntity.ok(Map.of(
            "token", token,
            "user", Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole())
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "username and password are required"));
        }

        User user = userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid username or password"));
        }

        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

        return ResponseEntity.ok(Map.of(
            "token", token,
            "user", Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole())
        ));
    }
}
