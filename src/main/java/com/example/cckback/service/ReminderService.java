package com.example.cckback.service;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.InterventionRepository;
import com.example.cckback.Repository.RendezVousRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderService {

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private NotificationService notificationService;
@Autowired
private InterventionRepository interventionRepository;
    @Scheduled(cron = "0 0 8 * * *") // Run daily at 8 AM
    public void sendAppointmentReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderThreshold = now.plusDays(3); // Remind 3 days in advance
        List<RendezVous> upcomingAppointments = rendezVousRepository
                .findByDateRendezVousBetweenAndNotificationEnvoyeeFalse(now, reminderThreshold);

        for (RendezVous rv : upcomingAppointments) {
            String message = String.format("Rappel : Vous avez un rendez-vous prévu le %s : %s.",
                    rv.getDateRendezVous(), rv.getDescription());

            // Notify technicians
            for (Technicien technicien : rv.getTechniciens()) {
                notificationService.createNotification(
                        technicien,
                        Notification.NotificationType.APPOINTMENT_REMINDER,
                        message,
                        null,
                        rv,
                        null
                );
            }

            // Notify admin
            if (rv.getAdministrateur() != null) {
                notificationService.createNotification(
                        rv.getAdministrateur(),
                        Notification.NotificationType.APPOINTMENT_REMINDER,
                        message,
                        null,
                        rv,
                        null
                );
            }

            // Mark notification as sent
            rv.setNotificationEnvoyee(true);
            rendezVousRepository.save(rv);
        }}
        @Scheduled(cron = "0 0 9 * * *") // Run daily at 9 AM
        public void sendPendingInterventionReminders() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime threshold = now.minusHours(24); // Consider interventions pending for 24+ hours
            List<Intervention> pendingInterventions = interventionRepository
                    .findByStatutAndDateDebutBefore(Statut.EN_COURS, threshold);

            for (Intervention intervention : pendingInterventions) {
                String message = String.format("Rappel : L'intervention #%d pour le capteur %s à %s est toujours en attente de résolution.",
                        intervention.getIdInterv(),
                        intervention.getAlerte().getCapteur().getNomComplet(),
                        intervention.getAlerte().getCapteur().getEmplacement());

                // Notify assigned technicians
                for (Technicien technicien : intervention.getTechniciens()) {
                    notificationService.createNotification(
                            technicien,
                            Notification.NotificationType.INTERVENTION_PENDING,
                            message,
                            intervention,
                            null,
                            intervention.getAlerte()
                    );
                }
            }
        }
    }
