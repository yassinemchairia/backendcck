package com.example.cckback;
import com.example.cckback.Entity.CalendrierDisponibilite;
import com.example.cckback.Entity.Technicien;
import com.example.cckback.Repository.CalendrierDisponibiliteRepository;
import com.example.cckback.Repository.TechnicienRepository;
import com.example.cckback.service.CalendrierDisponibiliteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CalendrierDisponibiliteServiceTestJnit {
    @Mock
    private TechnicienRepository technicienRepository;

    @Mock
    private CalendrierDisponibiliteRepository calendrierDisponibiliteRepository;

    @InjectMocks
    private CalendrierDisponibiliteService calendrierDisponibiliteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDisponibilitesParTechnicien_TechnicienExisteAvecDisponibilites() {
        // Données simulées
        Long technicienId = 1L;
        Technicien technicien = new Technicien();
        technicien.setIdUser(technicienId);

        CalendrierDisponibilite disponibilite1 = new CalendrierDisponibilite();
        disponibilite1.setTechnicien(technicien);
        disponibilite1.setDate(LocalDate.of(2024, 4, 1));
        disponibilite1.setDisponible(true);

        CalendrierDisponibilite disponibilite2 = new CalendrierDisponibilite();
        disponibilite2.setTechnicien(technicien);
        disponibilite2.setDate(LocalDate.of(2024, 4, 2));
        disponibilite2.setDisponible(false);

        List<CalendrierDisponibilite> disponibilites = Arrays.asList(disponibilite1, disponibilite2);

        // Simulation des comportements des repositories
        when(technicienRepository.findById(technicienId)).thenReturn(Optional.of(technicien));
        when(calendrierDisponibiliteRepository.findByTechnicien(technicien)).thenReturn(disponibilites);

        // Exécution de la méthode
        List<CalendrierDisponibilite> result = calendrierDisponibiliteService.getDisponibilitesParTechnicien(technicienId);

        // Vérifications
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(disponibilites, result);

        // Vérifier que les méthodes ont été appelées une seule fois
        verify(technicienRepository, times(1)).findById(technicienId);
        verify(calendrierDisponibiliteRepository, times(1)).findByTechnicien(technicien);
    }

    @Test
    void testGetDisponibilitesParTechnicien_TechnicienExisteSansDisponibilites() {
        // Données simulées
        Long technicienId = 2L;
        Technicien technicien = new Technicien();
        technicien.setIdUser(technicienId);

        // Simulation des comportements des repositories
        when(technicienRepository.findById(technicienId)).thenReturn(Optional.of(technicien));
        when(calendrierDisponibiliteRepository.findByTechnicien(technicien)).thenReturn(Collections.emptyList());

        // Exécution de la méthode
        List<CalendrierDisponibilite> result = calendrierDisponibiliteService.getDisponibilitesParTechnicien(technicienId);

        // Vérifications
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Vérifier que les méthodes ont été appelées une seule fois
        verify(technicienRepository, times(1)).findById(technicienId);
        verify(calendrierDisponibiliteRepository, times(1)).findByTechnicien(technicien);
    }

    @Test
    void testGetDisponibilitesParTechnicien_TechnicienNonTrouve() {
        // Données simulées
        Long technicienId = 10L;

        // Simulation des comportements des repositories
        when(technicienRepository.findById(technicienId)).thenReturn(Optional.empty());

        // Vérification que l'exception est bien levée
        Exception exception = assertThrows(RuntimeException.class, () -> {
            calendrierDisponibiliteService.getDisponibilitesParTechnicien(technicienId);
        });

        assertEquals("Technicien non trouvé.", exception.getMessage());

        // Vérifier que seul `findById` a été appelé et non `findByTechnicien`
        verify(technicienRepository, times(1)).findById(technicienId);
        verify(calendrierDisponibiliteRepository, never()).findByTechnicien(any());
    }
}