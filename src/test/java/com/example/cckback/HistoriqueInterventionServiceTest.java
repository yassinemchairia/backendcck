package com.example.cckback;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.HistoriqueInterventionRepository;
import com.example.cckback.Repository.InterventionRepository;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.dto.HistoriqueInterventionListDTO;
import com.example.cckback.service.EmailService;
import com.example.cckback.service.HistoriqueInterventionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoriqueInterventionServiceTest {

    @Mock
    private HistoriqueInterventionRepository historiqueRepository;

    @Mock
    private InterventionRepository interventionRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HistoriqueInterventionService historiqueService;

    private Intervention intervention;
    private HistoriqueIntervention historique;
    private Technicien technicien;

    @BeforeEach
    void setUp() {
        technicien = new Technicien();
        technicien.setIdUser(1L);
        technicien.setNom("Dupont");
        technicien.setPrenom("Jean");

        intervention = new Intervention();
        intervention.setIdInterv(1L);
        intervention.setStatut(Statut.EN_COURS);
        intervention.setTechniciens(Collections.singletonList(technicien));

        historique = new HistoriqueIntervention();
        historique.setIdHistoriqueIntervention(1L);
        historique.setIntervention(intervention);
        historique.setTechniciens(intervention.getTechniciens());
        historique.setDescription("Test description");
        historique.setRapport("Test rapport");
        historique.setStatut(Statut.EN_COURS);
        historique.setDateAction(LocalDateTime.now());
    }

    @Test
    void ajouterHistorique_ShouldCreateNewHistorique() {
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));
        when(historiqueRepository.save(any(HistoriqueIntervention.class))).thenReturn(historique);

        HistoriqueIntervention result = historiqueService.ajouterHistorique(
                1L, "Description", "Rapport", Statut.EN_COURS);

        assertNotNull(result);
        assertEquals(intervention, result.getIntervention());
        verify(historiqueRepository).save(any(HistoriqueIntervention.class));
    }

    @Test
    void ajouterHistorique_ShouldThrowWhenInterventionTerminated() {
        intervention.setStatut(Statut.TERMINEE);
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));

        assertThrows(IllegalStateException.class, () -> {
            historiqueService.ajouterHistorique(1L, "Desc", "Rapp", Statut.EN_COURS);
        });
    }

    @Test
    void getHistoriquesParIntervention_ShouldReturnDTOs() {
        when(historiqueRepository.findByIntervention_IdInterv(1L))
                .thenReturn(Collections.singletonList(historique));

        List<HistoriqueInterventionListDTO> result = historiqueService.getHistoriquesParIntervention(1L);

        assertEquals(1, result.size());
        assertEquals("Dupont Jean", result.get(0).getTechniciens().get(0));
    }

    @Test
    void searchHistoriquesByDescriptionOrRapport_ShouldFilterCorrectly() {
        when(historiqueRepository.findByDescriptionOrRapportContaining("test"))
                .thenReturn(Collections.singletonList(historique));

        List<HistoriqueInterventionListDTO> result =
                historiqueService.searchHistoriquesByDescriptionOrRapport("test");

        assertEquals(1, result.size());
        assertEquals("Test description", result.get(0).getDescription());
    }
}