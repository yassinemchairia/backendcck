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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RapportInterventionServiceTest {

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
    private Technicien technicien;
    private Administrateur admin;
    private Capteur capteur;
    private Alerte alerte;

    @BeforeEach
    void setUp() {
        capteur = new Capteur();
        capteur.setIdCapt(1L);
        capteur.setDepartement(Capteur.Departement.MANOUBA);
        capteur.setEmplacement("Salle 101");

        alerte = new Alerte();
        alerte.setIdAlerte(1L);
        alerte.setCapteur(capteur);

        technicien = new Technicien();
        technicien.setIdUser(1L);

        intervention = new Intervention();
        intervention.setIdInterv(1L);
        intervention.setAlerte(alerte);
        intervention.setTechniciens(Collections.singletonList(technicien));

        rapport = new RapportIntervention();
        rapport.setDetails("Réparation effectuée");
        rapport.setCoutIntervention(100.0);
        rapport.setSatisfaction(5);

        admin = new Administrateur();
        admin.setIdUser(2L);
    }

    @Test
    void ajouterRapport_ValidRapport_ShouldSaveAndNotify() {
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));
        when(rapportInterventionRepository.save(any(RapportIntervention.class))).thenReturn(rapport);
        when(interventionRepository.save(any(Intervention.class))).thenReturn(intervention);
        when(administrateurRepository.findAll()).thenReturn(Collections.singletonList(admin));

        RapportIntervention result = rapportInterventionService.ajouterRapport(1L, rapport);

        assertEquals(rapport, result);
        assertEquals(Statut.TERMINEE, intervention.getStatut());
        assertNotNull(intervention.getDateFin());
        verify(notificationService).createNotification(eq(admin), eq(Notification.NotificationType.INTERVENTION_COMPLETED),
                anyString(), eq(intervention), isNull(), eq(alerte));
        verify(rapportInterventionRepository).save(rapport);
        verify(interventionRepository).save(intervention);
    }

    @Test
    void ajouterRapport_InterventionNotFound_ShouldThrowException() {
        when(interventionRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                rapportInterventionService.ajouterRapport(1L, rapport));

        assertEquals("Intervention avec ID 1 introuvable !", exception.getMessage());
        verify(rapportInterventionRepository, never()).save(any());
    }

    @Test
    void ajouterRapport_InvalidDetails_ShouldThrowException() {
        rapport.setDetails("");
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                rapportInterventionService.ajouterRapport(1L, rapport));

        assertEquals("Le champ 'details' est obligatoire !", exception.getMessage());
        verify(rapportInterventionRepository, never()).save(any());
    }

    @Test
    void ajouterRapport_InvalidCout_ShouldThrowException() {
        rapport.setCoutIntervention(0);
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                rapportInterventionService.ajouterRapport(1L, rapport));

        assertEquals("Le champ 'coutIntervention' est obligatoire !", exception.getMessage());
        verify(rapportInterventionRepository, never()).save(any());
    }

    @Test
    void ajouterRapport_InvalidSatisfaction_ShouldThrowException() {
        rapport.setSatisfaction(6);
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                rapportInterventionService.ajouterRapport(1L, rapport));

        assertEquals("La satisfaction doit être entre 1 et 5 !", exception.getMessage());
        verify(rapportInterventionRepository, never()).save(any());
    }

    @Test
    void getRapportsByUserId_ShouldReturnRapports() {
        intervention.setRapport(rapport);
        when(interventionRepository.findByTechniciens_IdUser(1L)).thenReturn(Collections.singletonList(intervention));

        List<RapportIntervention> result = rapportInterventionService.getRapportsByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(rapport, result.get(0));
        verify(interventionRepository).findByTechniciens_IdUser(1L);
    }

    @Test
    void getRapportsByUserId_NoRapports_ShouldReturnEmptyList() {
        when(interventionRepository.findByTechniciens_IdUser(1L)).thenReturn(Collections.emptyList());

        List<RapportIntervention> result = rapportInterventionService.getRapportsByUserId(1L);

        assertTrue(result.isEmpty());
        verify(interventionRepository).findByTechniciens_IdUser(1L);
    }
}