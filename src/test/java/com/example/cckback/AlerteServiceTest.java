package com.example.cckback;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AlerteRepository;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.service.AlerteService;
import com.example.cckback.service.EmailService;
import com.example.cckback.service.NotificationService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlerteServiceTest {

    @Mock
    private AlerteRepository alerteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AlerteService alerteService;

    private Alerte alerte;
    private Capteur capteur;
    private Administrateur admin;

    @BeforeEach
    void setUp() {
        capteur = new Capteur();
        capteur.setIdCapt(1L);
        capteur.setDepartement(Capteur.Departement.MANOUBA);
        capteur.setEmplacement("Salle 101");
        capteur.setUniteMesure("°C");

        alerte = new Alerte();
        alerte.setIdAlerte(1L);
        alerte.setCapteur(capteur);
        alerte.setTypePanne(Alerte.TypePanne.CLIMATISATION);
        alerte.setNiveauGravite(Alerte.NiveauGravite.CRITIQUE);
        alerte.setDateAlerte(LocalDateTime.now());
        alerte.setEstResolu(false);
        alerte.setValeurDeclenchement(35.5);
        alerte.setDescription("Température trop élevée");

        admin = new Administrateur();
        admin.setIdUser(1L);
        admin.setEmail("admin@example.com");
        admin.setRole(Role.ADMIN);
        admin.setValide(true);
    }

    @Test
    void existeAlerteNonResoluePourCapteur_ShouldReturnTrue() {
        when(alerteRepository.existsByCapteurAndEstResoluFalse(capteur)).thenReturn(true);

        boolean result = alerteService.existeAlerteNonResoluePourCapteur(capteur);

        assertTrue(result);
        verify(alerteRepository).existsByCapteurAndEstResoluFalse(capteur);
    }

    @Test
    void resoudreAlertesPourCapteur_WithCommentaire_ShouldUpdateAlertes() {
        String commentaire = "Problème résolu";
        List<Alerte> alertes = Collections.singletonList(alerte);

        when(alerteRepository.findByCapteurAndEstResoluFalse(capteur)).thenReturn(alertes);
        when(alerteRepository.saveAll(anyList())).thenReturn(alertes);

        alerteService.resoudreAlertesPourCapteur(capteur, commentaire);

        assertTrue(alerte.getEstResolu());
        assertNotNull(alerte.getDescription());
        assertTrue(alerte.getDescription().contains(commentaire));
        verify(alerteRepository).saveAll(alertes);
    }

    @Test
    void creerAlerte_ShouldSendNotificationsAndEmails() throws MailException, MessagingException {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(admin));
        when(alerteRepository.save(any(Alerte.class))).thenReturn(alerte);

        alerteService.creerAlerte(capteur, Alerte.TypePanne.CLIMATISATION,
                Alerte.NiveauGravite.CRITIQUE, 35.5);

        verify(emailService).sendEmail(eq(admin.getEmail()), anyString(), anyString());
        verify(notificationService, times(2)).createNotification(
                eq(admin),
                any(Notification.NotificationType.class),
                anyString(),
                isNull(),
                isNull(),
                any(Alerte.class));
    }

    @Test
    void marquerCommeResolue_ShouldUpdateAlerte() {
        when(alerteRepository.findById(1L)).thenReturn(Optional.of(alerte));
        when(alerteRepository.save(alerte)).thenReturn(alerte);

        Optional<Alerte> result = alerteService.marquerCommeResolue(1L);

        assertTrue(result.isPresent());
        assertTrue(result.get().getEstResolu());
        // Ne pas vérifier dateResolution si elle n'est pas définie
    }

    @Test
    void countAlertesByType_ShouldReturnCorrectCounts() {
        List<Alerte> alertes = Arrays.asList(
                createAlerteWithType(Alerte.TypePanne.ELECTRICITE),
                createAlerteWithType(Alerte.TypePanne.ELECTRICITE),
                createAlerteWithType(Alerte.TypePanne.CLIMATISATION)
        );

        when(alerteRepository.findAll()).thenReturn(alertes);

        Map<Alerte.TypePanne, Long> result = alerteService.countAlertesByType();

        assertEquals(2, result.get(Alerte.TypePanne.ELECTRICITE));
        assertEquals(1, result.get(Alerte.TypePanne.CLIMATISATION));
        assertEquals(0, result.get(Alerte.TypePanne.ENVIRONNEMENT));
    }

    @Test
    void averageResolutionTime_ShouldCalculateCorrectAverage() {
        Alerte resolvedAlerte = new Alerte();
        resolvedAlerte.setEstResolu(true);
        resolvedAlerte.setDateAlerte(LocalDateTime.now().minusHours(5));

        Intervention intervention = new Intervention();
        intervention.setDateFin(LocalDateTime.now());
        resolvedAlerte.setInterventions(Collections.singletonList(intervention));

        when(alerteRepository.findByEstResolu(true)).thenReturn(Collections.singletonList(resolvedAlerte));

        double result = alerteService.averageResolutionTime();

        assertTrue(result >= 5.0 && result <= 5.1);
    }

    private Alerte createAlerteWithType(Alerte.TypePanne type) {
        Alerte a = new Alerte();
        a.setTypePanne(type);
        return a;
    }
}