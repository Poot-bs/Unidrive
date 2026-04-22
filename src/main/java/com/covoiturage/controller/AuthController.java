package com.covoiturage.controller;

import com.covoiturage.dto.LoginRequest;
import com.covoiturage.dto.RegisterRequest;
import com.covoiturage.model.User;
import com.covoiturage.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return authService.creerCompte(request);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        String token = authService.authentifier(request);
        User user = authService.getByEmail(request.getEmail().trim()).orElseThrow();
        return Map.of(
            "token", token,
            "userId", user.getIdentifiant(),
            "role", user.getRole().name(),
            "name", user.getNom(),
            "email", user.getEmail()
        );
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader("Authorization") String token) {
        authService.deconnecter(extractToken(token));
        return Map.of("status", "disconnected");
    }

    @GetMapping("/me")
    public Map<String, String> me(@RequestHeader("Authorization") String token) {
        User user = authService.getUtilisateurParToken(extractToken(token));
        return Map.of(
            "userId", user.getIdentifiant(),
            "role", user.getRole().name(),
            "name", user.getNom(),
            "email", user.getEmail(),
            "status", user.getStatus().name()
        );
    }

    @GetMapping("/users")
    public List<User> users() {
        return authService.getUsers();
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return "";
        }
        return authorizationHeader.substring("Bearer ".length());
    }
}
