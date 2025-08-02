package com.example.cckback;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AdministrateurRepository;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.service.EmailService;
import com.example.cckback.service.NotificationService;
import com.example.cckback.service.RegistrationService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdministrateurRepository administrateurRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    private Technicien technicien;
    private Administrateur admin;

    @BeforeEach
    void setUp() {
        technicien = new Technicien();
        technicien.setIdUser(1L);
        technicien.setNom("Doe");
        technicien.setPrenom("John");
        technicien.setEmail("john@example.com");
        technicien.setNumeroTelephone("123456789");
        technicien.setSpecialite(Specialite.ELECTRICITE);
        technicien.setRole(Role.TECHNICIEN);
        technicien.setValide(false);

        admin = new Administrateur();
        admin.setIdUser(2L);
        admin.setEmail("admin@example.com");
        admin.setRole(Role.ADMIN);
    }



    @Test
    void registerUser_EmailExists_ShouldThrowException() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(technicien));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                registrationService.registerUser("Doe", "John", "john@example.com",
                        "password", "123456789", Specialite.ELECTRICITE));

        assertEquals("Un utilisateur avec cet email existe déjà.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void validerTechnicien_Approve_ShouldValidateAndSendEmail() throws MessagingException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(technicien));
        when(userRepository.save(any(Technicien.class))).thenReturn(technicien);

        Utilisateur result = registrationService.validerTechnicien(1L, true);

        assertTrue(result.isValide());
        verify(emailService).sendEmail(eq("john@example.com"), eq("Votre inscription a été validée"), anyString());
        verify(userRepository).save(technicien);
    }

    @Test
    void validerTechnicien_Reject_ShouldDeleteAndSendEmail() throws MessagingException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(technicien));

        Utilisateur result = registrationService.validerTechnicien(1L, false);

        assertEquals(technicien, result);
        verify(emailService).sendEmail(eq("john@example.com"), eq("Votre inscription a été rejetée"), anyString());
        verify(userRepository).delete(technicien);
    }

    @Test
    void validerTechnicien_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                registrationService.validerTechnicien(1L, true));

        assertEquals("Utilisateur non trouvé", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).delete(any());
    }

    @Test
    void validerTechnicien_NotTechnicien_ShouldThrowException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                registrationService.validerTechnicien(2L, true));

        assertEquals("Ce n'est pas un technicien", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).delete(any());
    }
}