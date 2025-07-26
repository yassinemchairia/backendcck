package com.example.cckback;

import com.example.cckback.Entity.Specialite;
import com.example.cckback.Entity.Technicien;
import com.example.cckback.Repository.TechnicienRepository;
import com.example.cckback.service.TechnicienService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
class TechnicienServiceTestIntegration {

    @Autowired
    private TechnicienService technicienService;

    @Autowired
    private TechnicienRepository technicienRepository;

    @BeforeEach
    void setUp() {
        // Insérer des techniciens en base de données
        Technicien tech1 = new Technicien();
        tech1.setNom("Technicien 1");
        tech1.setSpecialite(Specialite.CLIMATISATION);
        technicienRepository.save(tech1);

        Technicien tech2 = new Technicien();
        tech2.setNom("Technicien 2");
        tech2.setSpecialite(Specialite.CLIMATISATION);
        technicienRepository.save(tech2);

        Technicien tech3 = new Technicien();
        tech3.setNom("Technicien 3");
        tech3.setSpecialite(Specialite.ELECTRICITE);
        technicienRepository.save(tech3);
    }

    @Test
    void testGetTechniciensParSpecialite() {
        // Appel de la méthode avec la spécialité CLIMATISATION
        List<Technicien> result = technicienService.getTechniciensParSpecialite(Specialite.CLIMATISATION);

        // Vérification que seulement les techniciens CLIMATISATION sont récupérés
        assertFalse(result.isEmpty());
        assertEquals(3, result.size());
        assertEquals(Specialite.CLIMATISATION, result.get(0).getSpecialite());
        assertEquals(Specialite.CLIMATISATION, result.get(1).getSpecialite());

    }
}

