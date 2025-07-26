package com.example.cckback;
import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AlerteRepository;
import com.example.cckback.Repository.InterventionRepository;
import com.example.cckback.Repository.TechnicienRepository;
import com.example.cckback.service.InterventionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InterventionServiceIntegrationTestIntegration {
    @Autowired
    private InterventionRepository interventionRepository;

    @Autowired
    private TechnicienRepository technicienRepository;

    @Autowired
    private AlerteRepository alerteRepository;

    @Autowired
    private InterventionService interventionService;

    private Alerte savedAlerte;
    private Technicien savedTechnicien1;
    private Technicien savedTechnicien2;

    @BeforeEach
    void setUp() {
        // Création d'une alerte
        savedAlerte = new Alerte();
        savedAlerte = alerteRepository.save(savedAlerte);

        // Création de techniciens
        savedTechnicien1 = new Technicien();
        savedTechnicien1.setNom("Technicien 1");
        savedTechnicien1 = technicienRepository.save(savedTechnicien1);

        savedTechnicien2 = new Technicien();
        savedTechnicien2.setNom("Technicien 2");
        savedTechnicien2 = technicienRepository.save(savedTechnicien2);
    }

    @Test
    @Order(1)
    void testAjouterIntervention_Success() {
        LocalDateTime dateDebut = LocalDateTime.of(2024, 4, 1, 10, 0);
        PrioriteIntervention priorite = PrioriteIntervention.ELEVEE;
        TypeIntervention typeIntervention = TypeIntervention.PREVENTIVE;
        List<Long> idTechniciens = List.of(savedTechnicien1.getIdUser(), savedTechnicien2.getIdUser());
        Long idAlerte = savedAlerte.getIdAlerte();

        Intervention intervention = interventionService.ajouterIntervention(dateDebut, priorite, typeIntervention, idTechniciens, idAlerte);

        assertNotNull(intervention);
        assertEquals(priorite, intervention.getPriorite());
        assertEquals(typeIntervention, intervention.getTypeIntervention());
        assertEquals(2, intervention.getTechniciens().size());
        assertEquals(idAlerte, intervention.getAlerte().getIdAlerte());
    }

    @Test
    @Order(2)
    void testAjouterIntervention_AlerteIntrouvable() {
        LocalDateTime dateDebut = LocalDateTime.of(2024, 4, 1, 10, 0);
        PrioriteIntervention priorite = PrioriteIntervention.BASSE;
        TypeIntervention typeIntervention = TypeIntervention.PREVENTIVE;
        List<Long> idTechniciens = List.of(savedTechnicien1.getIdUser(), savedTechnicien2.getIdUser());
        Long idAlerteInexistant = 999L; // ID qui n'existe pas

        Exception exception = assertThrows(RuntimeException.class, () -> {
            interventionService.ajouterIntervention(dateDebut, priorite, typeIntervention, idTechniciens, idAlerteInexistant);
        });

        assertEquals("Alerte introuvable avec l'ID : " + idAlerteInexistant, exception.getMessage());
    }
}
