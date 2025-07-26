package com.example.cckback;

import com.example.cckback.Entity.Specialite;
import com.example.cckback.Entity.Technicien;
import com.example.cckback.Repository.TechnicienRepository;
import com.example.cckback.service.TechnicienService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnicienServiceTestMokito {

    @Mock
    private TechnicienRepository technicienRepository;

    @InjectMocks
    private TechnicienService technicienService;

    @Test
    void testGetTechniciensParSpecialite() {
        // Création de faux techniciens
        Technicien tech1 = new Technicien();
        tech1.setIdUser(1L);
        tech1.setNom("Technicien 1");
        tech1.setSpecialite(Specialite.CLIMATISATION);

        Technicien tech2 = new Technicien();
        tech2.setIdUser(2L);
        tech2.setNom("Technicien 2");
        tech2.setSpecialite(Specialite.CLIMATISATION);

        List<Technicien> mockTechniciens = Arrays.asList(tech1, tech2);

        // Simulation du comportement du repository
        when(technicienRepository.findBySpecialite(Specialite.CLIMATISATION)).thenReturn(mockTechniciens);

        // Appel de la méthode à tester
        List<Technicien> result = technicienService.getTechniciensParSpecialite(Specialite.CLIMATISATION);

        // Vérification des résultats
        assertEquals(2, result.size());
        assertEquals("Technicien 1", result.get(0).getNom());
        assertEquals("Technicien 2", result.get(1).getNom());

        // Vérifier que le repository a bien été appelé une fois
        verify(technicienRepository, times(1)).findBySpecialite(Specialite.CLIMATISATION);
    }
}

