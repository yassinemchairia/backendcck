package com.example.cckback;
import com.example.cckback.Entity.CalendrierDisponibilite;
import com.example.cckback.Entity.Technicien;
import com.example.cckback.Repository.CalendrierDisponibiliteRepository;
import com.example.cckback.Repository.TechnicienRepository;
import com.example.cckback.service.CalendrierDisponibiliteService;
import com.example.cckback.service.TechnicienNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
public class ajouterDisponibiliteJUnit {
    @Mock
    private CalendrierDisponibiliteRepository calendrierDisponibiliteRepository;

    @Mock
    private TechnicienRepository technicienRepository;

    @InjectMocks
    private CalendrierDisponibiliteService calendrierDisponibiliteService;

    @BeforeEach
    void setUp() {
        // Initialisation des mocks avant chaque test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAjouterDisponibilite_technicienIntrouvable() {
        // Données simulées
        Long technicienId = 2L;
        LocalDate date = LocalDate.now();
        boolean disponible = true;

        // Simuler que la méthode findByIdUser retourne une valeur vide (aucun technicien trouvé)
        when(technicienRepository.findByIdUser(technicienId)).thenReturn(Optional.empty()); // Aucun technicien trouvé

        // Vérification que la méthode lance bien une exception lorsque le technicien n'est pas trouvé
        Exception exception = assertThrows(TechnicienNotFoundException.class, () -> {
            calendrierDisponibiliteService.ajouterDisponibilite(technicienId, date, disponible);
        });
        // Vérification du message d'exception
        assertEquals("Technicien non trouvé.", exception.getMessage());
    }

    @Test
    void testAjouterDisponibilite() {
        // Données simulées
        Long technicienId = 2L;
        LocalDate date = LocalDate.now();
        boolean disponible = true;

        Technicien technicien = new Technicien();
        technicien.setIdUser(technicienId);

        CalendrierDisponibilite calendrierDisponibilite = new CalendrierDisponibilite();
        calendrierDisponibilite.setTechnicien(technicien);
        calendrierDisponibilite.setDate(date);
        calendrierDisponibilite.setDisponible(disponible);

        // Simuler que la méthode findByIdUser retourne un technicien
        when(technicienRepository.findByIdUser(technicienId)).thenReturn(Optional.of(technicien)); // Simule que le technicien est trouvé

        // Simuler l'enregistrement de la disponibilité
        when(calendrierDisponibiliteRepository.findByTechnicienAndDate(technicien, date)).thenReturn(java.util.Collections.emptyList());
        when(calendrierDisponibiliteRepository.save(any(CalendrierDisponibilite.class))).thenReturn(calendrierDisponibilite);

        // Appeler la méthode à tester
        CalendrierDisponibilite result = calendrierDisponibiliteService.ajouterDisponibilite(technicienId, date, disponible);

        // Vérifications
        assertNotNull(result);
        assertEquals(technicienId, result.getTechnicien().getIdUser());
        assertEquals(date, result.getDate());
        assertTrue(result.isDisponible());
        verify(calendrierDisponibiliteRepository, times(1)).save(any(CalendrierDisponibilite.class));
    }
}