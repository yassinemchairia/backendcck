package com.example.cckback.service;

import com.example.cckback.Entity.Utilisateur;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.dto.TokenResponse;
import com.example.cckback.security.jwt.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, JwtService jwtService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public TokenResponse authenticate(String email, String password) {
        Utilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isValide()) {
            throw new RuntimeException("Your account has not been validated by the admin.");
        }

        if (passwordEncoder.matches(password, user.getMotDePasse())) {
            String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name(), user.getIdUser());
            // Pour l'exemple, je mets le refreshToken à null (à implémenter si besoin)
            return new TokenResponse(accessToken, null);
        } else {
            throw new BadCredentialsException("Invalid credentials");
        }
    }


}