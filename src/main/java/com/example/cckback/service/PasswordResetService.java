package com.example.cckback.service;

import com.example.cckback.Entity.Utilisateur;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.dto.PasswordResetRequest;
import com.example.cckback.security.jwt.JwtService;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public PasswordResetService(UserRepository userRepository,
                                EmailService emailService,
                                BCryptPasswordEncoder passwordEncoder,
                                JwtService jwtService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void requestPasswordReset(String email) throws MessagingException {
        Utilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void resetPassword(PasswordResetRequest request) {
        Utilisateur user = userRepository.findByEmailAndResetPasswordToken(request.getEmail(), request.getToken())
                .orElseThrow(() -> new RuntimeException("Lien de réinitialisation invalide"));

        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Le lien de réinitialisation a expiré");
        }

        user.setMotDePasse(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);
    }
}