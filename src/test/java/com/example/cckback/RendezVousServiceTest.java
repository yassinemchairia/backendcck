package com.example.cckback;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.*;
import com.example.cckback.dto.AutoPlanificationRequest;
import com.example.cckback.dto.RendezVousStatsDTO;
import com.example.cckback.service.NotificationService;
import com.example.cckback.service.RendezVousService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RendezVousServiceTest {

    @Mock
    private RendezVousRepository rendezVousRepository;

    @Mock
    private TechnicienRepository technicienRepository;

    @Mock
    private AdministrateurRepository administrateurRepository;

    @Mock
    private CalendrierDisponibiliteRepository calendrierDisponibiliteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RendezVousService rendezVousService;

    private Technicien technicien;
    private Administrateur admin;
    private CalendrierDisponibilite disponibilite;
    private RendezVous rendezVous;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        technicien = new Technicien();
        technicien.setIdUser(1L);
        technicien.setNom("Doe");
        technicien.setPrenom("John");
        technicien.setSpecialite(Specialite.ELECTRICITE);

        admin = new Administrateur();
        admin.setIdUser(2L);
        admin.setEmail("admin@example.com");

        disponibilite = new CalendrierDisponibilite();
        disponibilite.setTechnicien(technicien);
        disponibilite.setDate(LocalDate.now());
        disponibilite.setDisponible(true);

        rendezVous = new RendezVous();
        rendezVous.setIdRendezvous(1L);
        rendezVous.setDescription("Test RDV");
        rendezVous.setDateRendezVous(LocalDateTime.now());
        rendezVous.setTechniciens(Collections.singletonList(technicien));
        rendezVous.setAdministrateur(admin);

        // Inject @Autowired notificationService
        Field notificationServiceField = RendezVousService.class.getDeclaredField("notificationService");
        notificationServiceField.setAccessible(true);
        notificationServiceField.set(rendezVousService, notificationService);
    }



    @Test
    void suggererPlanification_NoTechnicians_ShouldThrowException() {
        AutoPlanificationRequest request = new AutoPlanificationRequest();
        request.setSpecialiteRequise(Specialite.ELECTRICITE);

        when(technicienRepository.findBySpecialite(Specialite.ELECTRICITE)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                rendezVousService.suggererPlanification(request));

        assertEquals("Aucun technicien disponible avec la spécialité requise : ELECTRICITE", exception.getMessage());
    }

    @Test
    void ajouterRendezVous_ValidInput_ShouldSaveAndNotify() {
        when(technicienRepository.findAllById(Collections.singletonList(1L))).thenReturn(Collections.singletonList(technicien));
        when(administrateurRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(rendezVousRepository.save(any(RendezVous.class))).thenReturn(rendezVous);

        RendezVous result = rendezVousService.ajouterRendezVous(2L, "Test RDV", LocalDateTime.now(), Collections.singletonList(1L));

        assertEquals(rendezVous, result);
        verify(notificationService, times(2)).createNotification(any(Utilisateur.class),
                eq(Notification.NotificationType.APPOINTMENT_ASSIGNED), anyString(), isNull(), eq(rendezVous), isNull());
        verify(rendezVousRepository).save(any(RendezVous.class));
    }

    @Test
    void ajouterRendezVous_NoTechnicians_ShouldThrowException() {
        when(technicienRepository.findAllById(Collections.singletonList(1L))).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                rendezVousService.ajouterRendezVous(2L, "Test RDV", LocalDateTime.now(), Collections.singletonList(1L)));

        assertEquals("Aucun technicien valide sélectionné", exception.getMessage());
        verify(rendezVousRepository, never()).save(any());
    }

    @Test
    void getRendezVousByUserId_ShouldReturnRendezVousList() {
        when(rendezVousRepository.findByUserId(1L)).thenReturn(Collections.singletonList(rendezVous));

        List<RendezVous> result = rendezVousService.getRendezVousByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(rendezVous, result.get(0));
        verify(rendezVousRepository).findByUserId(1L);
    }

    

    @Test
    void countRendezVousByWeek_ShouldReturnWeeklyCounts() {
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        when(rendezVousRepository.countByDateRendezVousBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(1L);

        Map<String, Long> result = rendezVousService.countRendezVousByWeek(2);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("S" + startDate.get(java.time.temporal.WeekFields.ISO.weekOfYear()) + " (" + startDate + " - " + startDate.plusDays(6) + ")"));
        verify(rendezVousRepository, times(2)).countByDateRendezVousBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void calculateParticipationRate_ShouldReturnCorrectRates() {
        when(technicienRepository.findAll()).thenReturn(Collections.singletonList(technicien));
        when(rendezVousRepository.countByTechniciensContaining(technicien)).thenReturn(2L);
        when(rendezVousRepository.countByTechniciensContainingAndNotificationEnvoyee(technicien, true)).thenReturn(1L);

        Map<String, Double> result = rendezVousService.calculateParticipationRate();

        assertEquals(50.0, result.get("Doe John"));
        verify(technicienRepository).findAll();
        verify(rendezVousRepository).countByTechniciensContaining(technicien);
        verify(rendezVousRepository).countByTechniciensContainingAndNotificationEnvoyee(technicien, true);
    }

    @Test
    void getRendezVousStats_ShouldReturnSortedStats() {
        when(technicienRepository.findAll()).thenReturn(Collections.singletonList(technicien));
        when(rendezVousRepository.countByTechniciensContaining(technicien)).thenReturn(2L);
        when(rendezVousRepository.countByTechniciensContainingAndNotificationEnvoyee(technicien, true)).thenReturn(1L);

        List<RendezVousStatsDTO> result = rendezVousService.getRendezVousStats();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getTechnicienId());
        assertEquals("Doe John", result.get(0).getNomComplet());
        assertEquals(2L, result.get(0).getNombreRdvsTotal());
        assertEquals(1L, result.get(0).getNombreRdvsPresents());
        assertEquals(50.0, result.get(0).getTauxParticipation());
        verify(technicienRepository).findAll();
    }
}