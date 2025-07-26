package com.example.cckback;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AdministrateurRepository;
import com.example.cckback.Repository.RendezVousRepository;
import com.example.cckback.Repository.TechnicienRepository;
import com.example.cckback.service.RendezVousService;
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

class RendezVousServiceTest {

    @Mock
    private RendezVousRepository rendezVousRepository;

    @Mock
    private TechnicienRepository technicienRepository;

    @Mock
    private AdministrateurRepository administrateurRepository;

    @InjectMocks
    private RendezVousService rendezVousService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAjouterRendezVous_Success() {
        Long adminId = 1L;
        List<Long> technicienIds = List.of(2L, 3L);
        LocalDateTime date = LocalDateTime.of(2024, 4, 5, 10, 0);
        String description = "Réunion de suivi";

        Administrateur admin = new Administrateur();
        admin.setIdUser(adminId);

        Technicien tech1 = new Technicien();
        tech1.setIdUser(2L);
        Technicien tech2 = new Technicien();
        tech2.setIdUser(3L);
        List<Technicien> techniciens = List.of(tech1, tech2);

        RendezVous rendezVous = new RendezVous();
        rendezVous.setAdministrateur(admin);
        rendezVous.setTechniciens(techniciens);
        rendezVous.setDateRendezVous(date);
        rendezVous.setDescription(description);

        when(administrateurRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(technicienRepository.findAllById(technicienIds)).thenReturn(techniciens);
        when(rendezVousRepository.save(any(RendezVous.class))).thenReturn(rendezVous);

        RendezVous result = rendezVousService.ajouterRendezVous(adminId, description, date, technicienIds);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(admin, result.getAdministrateur());
        assertEquals(techniciens, result.getTechniciens());
        assertEquals(date, result.getDateRendezVous());

        verify(administrateurRepository, times(1)).findById(adminId);
        verify(technicienRepository, times(1)).findAllById(technicienIds);
        verify(rendezVousRepository, times(1)).save(any(RendezVous.class));
    }

    @Test
    void testAjouterRendezVous_AdminIntrouvable() {
        Long adminId = 5L;
        List<Long> technicienIds = List.of(2L);
        LocalDateTime date = LocalDateTime.of(2024, 4, 5, 10, 0);
        String description = "Réunion critique";

        when(administrateurRepository.findById(adminId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            rendezVousService.ajouterRendezVous(adminId, description, date, technicienIds);
        });

        assertEquals("Administrateur non trouvé", exception.getMessage());

        verify(administrateurRepository, times(1)).findById(adminId);
        verify(technicienRepository, never()).findAllById(any());
        verify(rendezVousRepository, never()).save(any());
    }

    @Test
    void testAjouterRendezVous_AucunTechnicienTrouvé() {
        Long adminId = 1L;
        List<Long> technicienIds = List.of(999L);
        LocalDateTime date = LocalDateTime.of(2024, 4, 5, 10, 0);
        String description = "Maintenance";

        Administrateur admin = new Administrateur();
        admin.setIdUser(adminId);

        when(administrateurRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(technicienRepository.findAllById(technicienIds)).thenReturn(List.of());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            rendezVousService.ajouterRendezVous(adminId, description, date, technicienIds);
        });

        assertEquals("Aucun technicien valide sélectionné", exception.getMessage());

        verify(administrateurRepository, times(1)).findById(adminId);
        verify(technicienRepository, times(1)).findAllById(technicienIds);
        verify(rendezVousRepository, never()).save(any());
    }
}

