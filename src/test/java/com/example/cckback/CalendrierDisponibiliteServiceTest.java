package com.example.cckback;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.CalendrierDisponibiliteRepository;
import com.example.cckback.Repository.TechnicienRepository;
import com.example.cckback.dto.TechniciensDTO;

import com.example.cckback.service.CalendrierDisponibiliteService;
import com.example.cckback.service.TechnicienNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendrierDisponibiliteServiceTest {

    @Mock
    private CalendrierDisponibiliteRepository calendrierRepository;

    @Mock
    private TechnicienRepository technicienRepository;

    @InjectMocks
    private CalendrierDisponibiliteService calendrierService;

    private Technicien technicien;
    private CalendrierDisponibilite disponibilite;

    @BeforeEach
    void setUp() {
        technicien = new Technicien();
        technicien.setIdUser(1L);
        technicien.setNom("Dupont");
        technicien.setEmail("tech@example.com");
        technicien.setSpecialite(Specialite.ELECTRICITE);

        disponibilite = new CalendrierDisponibilite();
        disponibilite.setIdCalendrierDisponibilite(1L);
        disponibilite.setTechnicien(technicien);
        disponibilite.setDate(LocalDate.now());
        disponibilite.setDisponible(true);
    }

    @Test
    void ajouterDisponibilite_ShouldCreateNewDisponibilite() {
        when(technicienRepository.findById(1L)).thenReturn(Optional.of(technicien));
        when(calendrierRepository.findByTechnicienAndDate(technicien, LocalDate.now()))
                .thenReturn(Collections.emptyList());
        when(calendrierRepository.save(any(CalendrierDisponibilite.class))).thenReturn(disponibilite);

        CalendrierDisponibilite result = calendrierService.ajouterDisponibilite(1L, LocalDate.now(), true);

        assertNotNull(result);
        assertEquals(technicien, result.getTechnicien());
        verify(calendrierRepository).save(any(CalendrierDisponibilite.class));
    }

    @Test
    void ajouterDisponibilite_ShouldThrowWhenTechnicienNotFound() {
        when(technicienRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TechnicienNotFoundException.class, () -> {
            calendrierService.ajouterDisponibilite(1L, LocalDate.now(), true);
        });
    }

    @Test
    void getTechniciensDisponiblesPourDate_ShouldReturnDTOs() {
        when(calendrierRepository.findByDate(LocalDate.now()))
                .thenReturn(Collections.singletonList(disponibilite));

        List<TechniciensDTO> result = calendrierService.getTechniciensDisponiblesPourDate(LocalDate.now());

        assertEquals(1, result.size());
        assertEquals(technicien.getEmail(), result.get(0).getEmail());
    }

    @Test
    void getTechniciensDisponiblesParIntervalleEtSpecialite_ShouldFilterCorrectly() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(7);
        List<Long> techniciensDejaAffiches = new ArrayList<>();

        when(calendrierRepository.findByDateBetween(start, end))
                .thenReturn(Collections.singletonList(disponibilite));

        List<TechniciensDTO> result = calendrierService.getTechniciensDisponiblesParIntervalleEtSpecialite(
                start, end, Specialite.ELECTRICITE, techniciensDejaAffiches);

        assertEquals(1, result.size());
        assertEquals(1, techniciensDejaAffiches.size());
    }

    @Test
    void ajouterDisponibilitesEnMasse_ShouldCreateMultipleDisponibilites() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(2);

        when(technicienRepository.findById(1L)).thenReturn(Optional.of(technicien));
        // Retourner une liste vide pour n'importe quelle date avec ce technicien
        when(calendrierRepository.findByTechnicienAndDate(technicien, start))
                .thenReturn(Collections.emptyList());
        when(calendrierRepository.findByTechnicienAndDate(technicien, start.plusDays(1)))
                .thenReturn(Collections.emptyList());
        when(calendrierRepository.findByTechnicienAndDate(technicien, end))
                .thenReturn(Collections.emptyList());
        when(calendrierRepository.save(any(CalendrierDisponibilite.class))).thenReturn(disponibilite);

        List<CalendrierDisponibilite> result = calendrierService.ajouterDisponibilitesEnMasse(
                1L, start, end, true);

        assertEquals(3, result.size());
    }

    @Test
    void findByTechnicienIdAndDate_ShouldReturnDisponibilite() {
        when(technicienRepository.findById(1L)).thenReturn(Optional.of(technicien));
        when(calendrierRepository.findByTechnicienAndDate(technicien, LocalDate.now()))
                .thenReturn(Collections.singletonList(disponibilite));

        Optional<CalendrierDisponibilite> result = calendrierService.findByTechnicienIdAndDate(1L, LocalDate.now());

        assertTrue(result.isPresent());
        assertEquals(disponibilite.getIdCalendrierDisponibilite(), result.get().getIdCalendrierDisponibilite());
    }
}