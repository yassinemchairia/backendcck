package com.example.cckback;

import com.example.cckback.Entity.Technicien;
import com.example.cckback.Entity.Utilisateur;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.dto.PasswordResetRequest;
import com.example.cckback.security.jwt.JwtService;
import com.example.cckback.service.EmailService;
import com.example.cckback.service.PasswordResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private Utilisateur user;
    private PasswordResetRequest request;

    @BeforeEach
    void setUp() {
        user = new Technicien();
        user.setIdUser(1L);
        user.setEmail("user@example.com");
        user.setMotDePasse("oldPassword");

        request = new PasswordResetRequest();
        request.setEmail("user@example.com");
        request.setToken(UUID.randomUUID().toString());
        request.setNewPassword("newPassword");
    }

    @Test
    void requestPasswordReset_ShouldGenerateTokenAndSendEmail() throws Exception {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        passwordResetService.requestPasswordReset("user@example.com");

        assertNotNull(user.getResetPasswordToken());
        assertNotNull(user.getResetPasswordTokenExpiry());
        verify(emailService).sendPasswordResetEmail(eq("user@example.com"), anyString());
    }

    @Test
    void resetPassword_ShouldUpdatePasswordWhenValid() {
        user.setResetPasswordToken(request.getToken());
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));

        when(userRepository.findByEmailAndResetPasswordToken("user@example.com", request.getToken()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        passwordResetService.resetPassword(request);

        assertEquals("encodedNewPassword", user.getMotDePasse());
        assertNull(user.getResetPasswordToken());
        assertNull(user.getResetPasswordTokenExpiry());
    }

    @Test
    void resetPassword_ShouldThrowWhenTokenExpired() {
        user.setResetPasswordToken(request.getToken());
        user.setResetPasswordTokenExpiry(LocalDateTime.now().minusHours(1));

        when(userRepository.findByEmailAndResetPasswordToken("user@example.com", request.getToken()))
                .thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> {
            passwordResetService.resetPassword(request);
        });
    }
}