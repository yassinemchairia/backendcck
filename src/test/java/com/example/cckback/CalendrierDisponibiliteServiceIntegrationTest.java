package com.example.cckback;

import com.example.cckback.Entity.CalendrierDisponibilite;
import com.example.cckback.Entity.Technicien;
import com.example.cckback.Repository.CalendrierDisponibiliteRepository;
import com.example.cckback.Repository.TechnicienRepository;
import com.example.cckback.service.CalendrierDisponibiliteService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Charge le contexte Spring complet
@Transactional // Garantit que chaque test s'exécute dans une transaction isolée
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Permet d'exécuter les tests dans un ordre défini
public class CalendrierDisponibiliteServiceIntegrationTest {

    @Autowired
    private TechnicienRepository technicienRepository;

    @Autowired
    private CalendrierDisponibiliteRepository calendrierDisponibiliteRepository;

    @Autowired
    private CalendrierDisponibiliteService calendrierDisponibiliteService;

    private Technicien savedTechnicien;

    @BeforeEach
    void setUp() {
        // Créer et enregistrer un technicien dans la base
        Technicien technicien = new Technicien();
        technicien.setNom("Technicien Test");
        savedTechnicien = technicienRepository.save(technicien);

        // Ajouter des disponibilités pour ce technicien
        CalendrierDisponibilite dispo1 = new CalendrierDisponibilite();
        dispo1.setTechnicien(savedTechnicien);
        dispo1.setDate(LocalDate.of(2024, 4, 1));
        dispo1.setDisponible(true);

        CalendrierDisponibilite dispo2 = new CalendrierDisponibilite();
        dispo2.setTechnicien(savedTechnicien);
        dispo2.setDate(LocalDate.of(2024, 4, 2));
        dispo2.setDisponible(false);

        calendrierDisponibiliteRepository.save(dispo1);
        calendrierDisponibiliteRepository.save(dispo2);
    }

    @Test
    @Order(1)
    void testGetDisponibilitesParTechnicien_TechnicienExisteAvecDisponibilites() {
        // Exécution de la méthode
        List<CalendrierDisponibilite> result = calendrierDisponibiliteService.getDisponibilitesParTechnicien(savedTechnicien.getIdUser());

        // Vérifications
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @Order(2)
    void testGetDisponibilitesParTechnicien_TechnicienNonTrouve() {
        // Tester avec un ID inexistant
        Long technicienInexistantId = 999L;

        // Vérifier que l'exception est bien levée
        Exception exception = assertThrows(RuntimeException.class, () -> {
            calendrierDisponibiliteService.getDisponibilitesParTechnicien(technicienInexistantId);
        });

        assertEquals("Technicien non trouvé.", exception.getMessage());
    }

    @AfterEach
    void tearDown() {
        // Nettoyer la base après chaque test
        calendrierDisponibiliteRepository.deleteAll();
        technicienRepository.deleteAll();
    }
}
