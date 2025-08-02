package com.example.cckback;



import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AlerteRepository;
import com.example.cckback.Repository.InterventionRepository;
import com.example.cckback.Repository.TechnicienRepository;
import com.example.cckback.dto.InterventionCalendarDTO;
import com.example.cckback.dto.InterventionDTO;
import com.example.cckback.dto.TechnicienDTO;
import com.example.cckback.service.InterventionService;
import com.example.cckback.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterventionServiceTest {

    @Mock
    private InterventionRepository interventionRepository;

    @Mock
    private TechnicienRepository technicienRepository;

    @Mock
    private AlerteRepository alerteRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private InterventionService interventionService;

    private Intervention intervention;
    private Technicien technicien;
    private Alerte alerte;

    @BeforeEach
    void setUp() {
        technicien = new Technicien();
        technicien.setIdUser(1L);
        technicien.setNom("Dupont");
        technicien.setPrenom("Jean");
        technicien.setSpecialite(Specialite.ELECTRICITE);

        alerte = new Alerte();
        alerte.setIdAlerte(1L);
        Capteur capteur = new Capteur();
        capteur.setDepartement(Capteur.Departement.MANOUBA);
        capteur.setEmplacement("Salle 101");
        alerte.setCapteur(capteur);

        intervention = new Intervention();
        intervention.setIdInterv(1L);
        intervention.setTechniciens(Collections.singletonList(technicien));
        intervention.setAlerte(alerte);
        intervention.setDateDebut(LocalDateTime.now());
        intervention.setStatut(Statut.EN_COURS);
        intervention.setTypeIntervention(TypeIntervention.CORRECTIVE);
        intervention.setPriorite(PrioriteIntervention.MOYENNE);
    }

  

    @Test
    void getInterventionsForCalendar_ShouldFilterByDate() {
        when(technicienRepository.findById(1L)).thenReturn(Optional.of(technicien));
        intervention.setDateDebut(LocalDateTime.now());
        technicien.setInterventions(Collections.singletonList(intervention));

        List<InterventionCalendarDTO> result = interventionService.getInterventionsForCalendar(
                1L, LocalDate.now(), LocalDate.now());

        assertEquals(1, result.size());
        assertTrue(result.get(0).getTitle().contains("Intervention"));
    }

    @Test
    void countInterventionsByType_ShouldReturnCorrectCounts() {
        List<Intervention> interventions = Arrays.asList(
                createInterventionWithType(TypeIntervention.CORRECTIVE),
                createInterventionWithType(TypeIntervention.CORRECTIVE),
                createInterventionWithType(TypeIntervention.PREVENTIVE)
        );

        when(interventionRepository.findAll()).thenReturn(interventions);

        Map<TypeIntervention, Long> result = interventionService.countInterventionsByType();

        assertEquals(2, result.get(TypeIntervention.CORRECTIVE));
        assertEquals(1, result.get(TypeIntervention.PREVENTIVE));
    }

    @Test
    void calculateAverageInterventionDuration_ShouldReturnCorrectAverage() {
        Intervention completed = new Intervention();
        completed.setStatut(Statut.TERMINEE);
        completed.setDateDebut(LocalDateTime.now().minusHours(2));
        completed.setDateFin(LocalDateTime.now());

        when(interventionRepository.findByStatut(Statut.TERMINEE))
                .thenReturn(Collections.singletonList(completed));

        double result = interventionService.calculateAverageInterventionDuration();

        assertTrue(result >= 2.0 && result <= 2.1);
    }

    private Intervention createInterventionWithType(TypeIntervention type) {
        Intervention i = new Intervention();
        i.setTypeIntervention(type);
        return i;
    }
}