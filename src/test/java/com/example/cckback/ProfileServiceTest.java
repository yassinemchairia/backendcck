package com.example.cckback;

import com.example.cckback.Entity.Technicien;
import com.example.cckback.Entity.Utilisateur;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.dto.UpdateProfileRequest;
import com.example.cckback.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileService profileService;

    private Technicien technicien;
    private UpdateProfileRequest request;

    @BeforeEach
    void setUp() {
        technicien = new Technicien();
        technicien.setIdUser(1L);
        technicien.setNom("Doe");
        technicien.setPrenom("John");
        technicien.setEmail("john@example.com");
        technicien.setNumeroTelephone("123456789");

        request = new UpdateProfileRequest();
        request.setNom("Smith");
        request.setPrenom("Jane");
        request.setNumeroTelephone("987654321");
    }

    @Test
    void updateUserProfile_ShouldUpdateTechnicienAndSave() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(technicien));
        when(userRepository.save(any(Utilisateur.class))).thenReturn(technicien);

        Utilisateur result = profileService.updateUserProfile(1L, request);

        assertEquals("Smith", result.getNom());
        assertEquals("Jane", result.getPrenom());
        assertEquals("987654321", ((Technicien) result).getNumeroTelephone());
        verify(userRepository).save(technicien);
    }

    @Test
    void updateUserProfile_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                profileService.updateUserProfile(1L, request));

        assertEquals("Utilisateur non trouvé", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getCurrentUserProfile_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(technicien));

        Utilisateur result = profileService.getCurrentUserProfile(1L);

        assertEquals(technicien, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void getCurrentUserProfile_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                profileService.getCurrentUserProfile(1L));

        assertEquals("Utilisateur non trouvé", exception.getMessage());
    }
}