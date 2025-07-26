package com.example.cckback;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AdministrateurRepository;
import com.example.cckback.Repository.InterventionRepository;
import com.example.cckback.Repository.RapportInterventionRepository;
import com.example.cckback.service.NotificationService;
import com.example.cckback.service.RapportInterventionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RapportInterventionServiceTest {

    @Mock
    private RapportInterventionRepository rapportInterventionRepository;

    @Mock
    private InterventionRepository interventionRepository;

    @Mock
    private AdministrateurRepository administrateurRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RapportInterventionService rapportInterventionService;

    private Intervention intervention;
    private RapportIntervention rapport;
    private Administrateur admin;
    private Alerte alerte;
    private Capteur capteur;

    @BeforeEach
    void setUp() {
        capteur = new Capteur();
        capteur.setDepartement(Capteur.Departement.MANOUBA);
        capteur.setEmplacement("Salle 1");

        alerte = new Alerte();
        alerte.setIdAlerte(1L);
        alerte.setCapteur(capteur);

        intervention = new Intervention();
        intervention.setIdInterv(1L);
        intervention.setAlerte(alerte);

        rapport = new RapportIntervention();
        rapport.setDetails("Intervention terminée");
        rapport.setCoutIntervention(100.0);
        rapport.setSatisfaction(4);

        admin = new Administrateur();
        admin.setIdUser(1L);
    }

    @Test
    void testAjouterRapport_Success() {
        // Arrange
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));
        when(rapportInterventionRepository.save(any(RapportIntervention.class))).thenReturn(rapport);
        when(interventionRepository.save(any(Intervention.class))).thenReturn(intervention);
        when(administrateurRepository.findAll()).thenReturn(Collections.singletonList(admin));

        // Act
        RapportIntervention result = rapportInterventionService.ajouterRapport(1L, rapport);

        // Assert
        assertNotNull(result);
        assertEquals(intervention, result.getIntervention());
        assertEquals(Statut.TERMINEE, intervention.getStatut());
        assertNotNull(intervention.getDateFin());
        verify(rapportInterventionRepository).save(rapport);
        verify(interventionRepository).save(intervention);
        verify(notificationService).createNotification(eq(admin), eq(Notification.NotificationType.INTERVENTION_COMPLETED), anyString(), eq(intervention), eq(null), eq(alerte));
    }

    @Test
    void testAjouterRapport_InterventionNotFound() {
        // Arrange
        when(interventionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> rapportInterventionService.ajouterRapport(1L, rapport));
    }

    @Test
    void testAjouterRapport_InvalidDetails() {
        // Arrange
        rapport.setDetails("");
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> rapportInterventionService.ajouterRapport(1L, rapport));
    }

    @Test
    void testAjouterRapport_InvalidCout() {
        // Arrange
        rapport.setCoutIntervention(0.0);
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> rapportInterventionService.ajouterRapport(1L, rapport));
    }

    @Test
    void testAjouterRapport_InvalidSatisfaction() {
        // Arrange
        rapport.setSatisfaction(6);
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> rapportInterventionService.ajouterRapport(1L, rapport));
    }

    @Test
    void testGetRapportsByUserId() {
        // Arrange
        intervention.setRapport(rapport);
        when(interventionRepository.findByTechniciens_IdUser(1L)).thenReturn(Collections.singletonList(intervention));

        // Act
        List<RapportIntervention> result = rapportInterventionService.getRapportsByUserId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(rapport, result.get(0));
        verify(interventionRepository).findByTechniciens_IdUser(1L);
    }

    @Test
    void testGetRapportsByUserId_NoRapports() {
        // Arrange
        when(interventionRepository.findByTechniciens_IdUser(1L)).thenReturn(Collections.emptyList());

        // Act
        List<RapportIntervention> result = rapportInterventionService.getRapportsByUserId(1L);

        // Assert
        assertTrue(result.isEmpty());
        verify(interventionRepository).findByTechniciens_IdUser(1L);
    }
}