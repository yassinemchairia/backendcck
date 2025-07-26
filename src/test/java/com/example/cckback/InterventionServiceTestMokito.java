package com.example.cckback;
import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AlerteRepository;
import com.example.cckback.Repository.InterventionRepository;
import com.example.cckback.Repository.TechnicienRepository;
import com.example.cckback.service.InterventionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class InterventionServiceTestMokito {
    @Mock
    private InterventionRepository interventionRepository;

    @Mock
    private TechnicienRepository technicienRepository;

    @Mock
    private AlerteRepository alerteRepository;

    @InjectMocks
    private InterventionService interventionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAjouterIntervention_Success() {
        // Données de test
        LocalDateTime dateDebut = LocalDateTime.of(2024, 4, 1, 10, 0);
        PrioriteIntervention priorite = PrioriteIntervention.BASSE;
        TypeIntervention typeIntervention = TypeIntervention.CORRECTIVE;
        Long idAlerte = 1L;
        List<Long> idTechniciens = List.of(2L, 3L);

        // Mocks
        Alerte alerte = new Alerte();
        when(alerteRepository.findById(idAlerte)).thenReturn(Optional.of(alerte));

        Technicien technicien1 = new Technicien();
        technicien1.setIdUser(2L);
        Technicien technicien2 = new Technicien();
        technicien2.setIdUser(3L);
        when(technicienRepository.findAllById(idTechniciens)).thenReturn(List.of(technicien1, technicien2));

        Intervention intervention = new Intervention();
        when(interventionRepository.save(any(Intervention.class))).thenReturn(intervention);

        // Exécution de la méthode
        Intervention result = interventionService.ajouterIntervention(dateDebut, priorite, typeIntervention, idTechniciens, idAlerte);

        // Vérifications
        assertNotNull(result);
        verify(alerteRepository, times(1)).findById(idAlerte);
        verify(technicienRepository, times(1)).findAllById(idTechniciens);
        verify(interventionRepository, times(1)).save(any(Intervention.class));
    }

    @Test
    void testAjouterIntervention_AlerteIntrouvable() {
        LocalDateTime dateDebut = LocalDateTime.of(2024, 4, 1, 10, 0);
        PrioriteIntervention priorite = PrioriteIntervention.ELEVEE;
        TypeIntervention typeIntervention = TypeIntervention.PREVENTIVE;
        Long idAlerte = 1L;
        List<Long> idTechniciens = List.of(2L, 3L);

        // Simulation de l'absence d'alerte
        when(alerteRepository.findById(idAlerte)).thenReturn(Optional.empty());

        // Vérification de l'exception
        Exception exception = assertThrows(RuntimeException.class, () -> {
            interventionService.ajouterIntervention(dateDebut, priorite, typeIntervention, idTechniciens, idAlerte);
        });

        assertEquals("Alerte introuvable avec l'ID : " + idAlerte, exception.getMessage());
        verify(alerteRepository, times(1)).findById(idAlerte);
        verify(technicienRepository, never()).findAllById(any());
        verify(interventionRepository, never()).save(any());
    }
}
