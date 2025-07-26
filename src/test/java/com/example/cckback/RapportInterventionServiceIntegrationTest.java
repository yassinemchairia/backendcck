package com.example.cckback;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.InterventionRepository;
import com.example.cckback.Repository.RapportInterventionRepository;
import com.example.cckback.service.RapportInterventionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RapportInterventionServiceIntegrationTest {

    @Autowired
    private RapportInterventionRepository rapportInterventionRepository;

    @Autowired
    private InterventionRepository interventionRepository;

    @Autowired
    private RapportInterventionService rapportInterventionService;

    private Intervention savedIntervention;

    @BeforeEach
    void setUp() {
        // Création d'une intervention test
        savedIntervention = new Intervention();
        savedIntervention.setStatut(Statut.EN_COURS);
        savedIntervention = interventionRepository.save(savedIntervention);
    }

    @Test
    @Order(1)
    void testAjouterRapport_Success() {
        RapportIntervention rapport = new RapportIntervention();
        rapport.setDetails("Réparation complète");
        rapport.setCoutIntervention(300.0);
        rapport.setSatisfaction(5);

        RapportIntervention result = rapportInterventionService.ajouterRapport(savedIntervention.getIdInterv(), rapport);

        assertNotNull(result);
        assertEquals(Statut.TERMINEE, savedIntervention.getStatut());
        assertNotNull(savedIntervention.getDateFin());
    }

    @Test
    @Order(2)
    void testAjouterRapport_InterventionIntrouvable() {
        RapportIntervention rapport = new RapportIntervention();
        rapport.setDetails("Réparation complète");
        rapport.setCoutIntervention(300.0);
        rapport.setSatisfaction(5);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            rapportInterventionService.ajouterRapport(999L, rapport);
        });

        assertEquals("Intervention avec ID 999 introuvable !", exception.getMessage());
    }

    @Test
    @Order(3)
    void testAjouterRapport_DetailsManquants() {
        RapportIntervention rapport = new RapportIntervention();
        rapport.setCoutIntervention(300.0);
        rapport.setSatisfaction(4);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            rapportInterventionService.ajouterRapport(savedIntervention.getIdInterv(), rapport);
        });

        assertEquals("Le champ 'details' est obligatoire !", exception.getMessage());
    }
}

