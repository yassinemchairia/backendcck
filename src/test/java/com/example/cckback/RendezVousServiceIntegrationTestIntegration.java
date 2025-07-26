package com.example.cckback;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AdministrateurRepository;
import com.example.cckback.Repository.RendezVousRepository;
import com.example.cckback.Repository.TechnicienRepository;
import com.example.cckback.service.RendezVousService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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
class RendezVousServiceIntegrationTestIntegration {

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private TechnicienRepository technicienRepository;

    @Autowired
    private AdministrateurRepository administrateurRepository;

    @Autowired
    private RendezVousService rendezVousService;

    private Administrateur savedAdmin;
    private Technicien savedTechnicien;

    @BeforeEach
    void setUp() {
        savedAdmin = new Administrateur();
        savedAdmin = administrateurRepository.save(savedAdmin);

        savedTechnicien = new Technicien();
        savedTechnicien = technicienRepository.save(savedTechnicien);
    }

    @Test
    @Order(1)
    void testAjouterRendezVous_Success() {
        String description = "Réunion d'équipe";
        LocalDateTime date = LocalDateTime.of(2024, 4, 10, 14, 0);
        List<Long> technicienIds = List.of(savedTechnicien.getIdUser());

        RendezVous result = rendezVousService.ajouterRendezVous(savedAdmin.getIdUser(), description, date, technicienIds);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(savedAdmin, result.getAdministrateur());
        assertEquals(1, result.getTechniciens().size());
        assertEquals(date, result.getDateRendezVous());
    }

    @Test
    @Order(2)
    void testAjouterRendezVous_AdminIntrouvable() {
        String description = "Réunion critique";
        LocalDateTime date = LocalDateTime.of(2024, 4, 5, 10, 0);
        List<Long> technicienIds = List.of(savedTechnicien.getIdUser());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            rendezVousService.ajouterRendezVous(999L, description, date, technicienIds);
        });

        assertEquals("Administrateur non trouvé", exception.getMessage());
    }

    @Test
    @Order(3)
    void testAjouterRendezVous_AucunTechnicienTrouvé() {
        String description = "Maintenance réseau";
        LocalDateTime date = LocalDateTime.of(2024, 4, 5, 10, 0);
        List<Long> technicienIds = List.of(999L); // ID inexistant

        Exception exception = assertThrows(RuntimeException.class, () -> {
            rendezVousService.ajouterRendezVous(savedAdmin.getIdUser(), description, date, technicienIds);
        });

        assertEquals("Aucun technicien valide sélectionné", exception.getMessage());
    }
}

