package com.example.cckback;



import com.example.cckback.Entity.Administrateur;
import com.example.cckback.Entity.Role;
import com.example.cckback.Entity.Technicien;
import com.example.cckback.Entity.Utilisateur;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.dto.TokenResponse;
import com.example.cckback.security.jwt.JwtService;
import com.example.cckback.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    private Administrateur admin; // Changé de Utilisateur à Administrateur
private Technicien technicien;
    @BeforeEach
    void setUp() {
        admin = new Administrateur(); // Utilisation de la classe concrète
        admin.setEmail("test@example.com");
        admin.setMotDePasse("encodedPassword");
        admin.setRole(Role.ADMIN);
        admin.setValide(true);
        admin.setDepartement("IT"); // Ajout des propriétés spécifiques à Administrateur
    }

    @Test
    void authenticate_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("test@example.com", "ADMIN", admin.getIdUser())).thenReturn("jwtToken");

        TokenResponse response = authenticationService.authenticate("test@example.com", "password");

        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
    }

    @Test
    void authenticate_UserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            authenticationService.authenticate("unknown@example.com", "password");
        });
    }

    @Test
    void authenticate_InvalidPassword() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            authenticationService.authenticate("test@example.com", "wrongPassword");
        });
    }

    @Test
    void authenticate_AccountNotValidated() {
        admin.setValide(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(admin));
        // Supprimer la ligne suivante car elle n'est pas nécessaire
        // when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            authenticationService.authenticate("test@example.com", "password");
        });
    }
}