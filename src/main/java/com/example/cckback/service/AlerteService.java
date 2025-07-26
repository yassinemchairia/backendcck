package com.example.cckback.service;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AlerteRepository;
import com.example.cckback.Repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlerteService {

    private final AlerteRepository alerteRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public AlerteService(AlerteRepository alerteRepository, UserRepository userRepository,
                         EmailService emailService, NotificationService notificationService) {
        this.alerteRepository = alerteRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    public boolean existeAlerteNonResoluePourCapteur(Capteur capteur) {
        return alerteRepository.existsByCapteurAndEstResoluFalse(capteur);
    }

    @Transactional
    public void resoudreAlertesPourCapteur(Capteur capteur, String commentaire) {
        List<Alerte> alertes = alerteRepository.findByCapteurAndEstResoluFalse(capteur);

        alertes.forEach(alerte -> {
            alerte.resoudre();
            if (commentaire != null) {
                alerte.setDescription(alerte.getDescription() + "\nRésolution: " + commentaire);
            }
        });

        alerteRepository.saveAll(alertes);
    }

    @Transactional
    public Alerte creerAlertes(Alerte alerte) {
        Alerte savedAlerte = alerteRepository.save(alerte);

        // Notify admins via email and WebSocket
        List<Utilisateur> admins = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .collect(Collectors.toList());
        String subject = "🚨 Nouvelle alerte détectée";
        String body = "<p>Une nouvelle alerte a été déclenchée par le capteur <strong>" + alerte.getCapteur().getEmplacement() + "</strong>.</p>"
                + "<p><strong>Type de panne :</strong> " + alerte.getTypePanne() + "</p>"
                + "<p><strong>Gravité :</strong> " + alerte.getNiveauGravite() + "</p>"
                + "<p><strong>Valeur déclenchée :</strong> " + alerte.getValeurDeclenchement() + " " + alerte.getCapteur().getUniteMesure() + "</p>"
                + "<p><strong>Date :</strong> " + alerte.getDateAlerte() + "</p>";

        for (Utilisateur admin : admins) {
            // Send email
            try {
                emailService.sendEmail(admin.getEmail(), subject, body);
            } catch (MailException | MessagingException e) {
                System.err.println("Erreur lors de l'envoi de l'email à : " + admin.getEmail());
                e.printStackTrace();
            }

            // Send NEW_ALERT_GENERATED notification
            String message = String.format("Nouvelle alerte détectée : %s (Type: %s, Gravité: %s, Valeur: %.2f %s)",
                    alerte.getCapteur().getNomComplet(),
                    alerte.getTypePanne(),
                    alerte.getNiveauGravite(),
                    alerte.getValeurDeclenchement(),
                    alerte.getCapteur().getUniteMesure());
            notificationService.createNotification(
                    admin,
                    Notification.NotificationType.NEW_ALERT_GENERATED,
                    message,
                    null,
                    null,
                    savedAlerte
            );

            // Send HIGH_PRIORITY_ALERT notification if severity is CRITIQUE or HIGH_CRITICAL
            if (alerte.getNiveauGravite() == Alerte.NiveauGravite.CRITIQUE ||
                    alerte.getNiveauGravite() == Alerte.NiveauGravite.HIGH_CRITICAL) {
                String highPriorityMessage = String.format("Alerte critique détectée : %s (Type: %s, Gravité: %s, Valeur: %.2f %s)",
                        alerte.getCapteur().getNomComplet(),
                        alerte.getTypePanne(),
                        alerte.getNiveauGravite(),
                        alerte.getValeurDeclenchement(),
                        alerte.getCapteur().getUniteMesure());
                notificationService.createNotification(
                        admin,
                        Notification.NotificationType.HIGH_PRIORITY_ALERT,
                        highPriorityMessage,
                        null,
                        null,
                        savedAlerte
                );
            }
        }

        return savedAlerte;
    }
    public Alerte findById(Long idAlerte) {
        return alerteRepository.findById(idAlerte).orElse(null);
    }
    @Transactional
    public void creerAlerte(Capteur capteur, Alerte.TypePanne typePanne, Alerte.NiveauGravite niveauGravite, Double valeurDeclenchement) {
        // Création de l'alerte
        Alerte alerte = new Alerte();
        alerte.setCapteur(capteur);
        alerte.setTypePanne(typePanne);
        alerte.setNiveauGravite(niveauGravite);
        alerte.setDateAlerte(LocalDateTime.now());
        alerte.setEstResolu(false);
        alerte.setValeurDeclenchement(valeurDeclenchement);
        alerte.setDescription(String.format("%s anormal détecté: %.2f %s (%s - %s)",
                capteur.getType(),
                valeurDeclenchement,
                capteur.getUniteMesure(),
                niveauGravite,
                capteur.getDepartement()));

        Alerte savedAlerte = alerteRepository.save(alerte);

        // Notify admins via email and WebSocket
        List<Utilisateur> admins = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .collect(Collectors.toList());

        String subject = "🚨 Nouvelle alerte détectée";
        String body = "<p>Une nouvelle alerte a été déclenchée par le capteur <strong>" + capteur.getEmplacement() + "</strong>.</p>"
                + "<p><strong>Type de panne :</strong> " + typePanne + "</p>"
                + "<p><strong>Gravité :</strong> " + niveauGravite + "</p>"
                + "<p><strong>Valeur déclenchée :</strong> " + valeurDeclenchement + " " + capteur.getUniteMesure() + "</p>"
                + "<p><strong>Date :</strong> " + alerte.getDateAlerte() + "</p>";

        for (Utilisateur admin : admins) {
            // Send email
            try {
                emailService.sendEmail(admin.getEmail(), subject, body);
            } catch (MailException | MessagingException e) {
                System.err.println("Erreur lors de l'envoi de l'email à : " + admin.getEmail());
                e.printStackTrace();
            }

            // Send NEW_ALERT_GENERATED notification
            String message = String.format("Nouvelle alerte détectée : %s (Type: %s, Gravité: %s, Valeur: %.2f %s)",
                    alerte.getCapteur().getNomComplet(),
                    alerte.getTypePanne(),
                    alerte.getNiveauGravite(),
                    alerte.getValeurDeclenchement(),
                    alerte.getCapteur().getUniteMesure());
            notificationService.createNotification(
                    admin,
                    Notification.NotificationType.NEW_ALERT_GENERATED,
                    message,
                    null,
                    null,
                    savedAlerte
            );

            // Send HIGH_PRIORITY_ALERT notification if severity is CRITIQUE or HIGH_CRITICAL
            if (niveauGravite == Alerte.NiveauGravite.CRITIQUE ||
                    niveauGravite == Alerte.NiveauGravite.HIGH_CRITICAL) {
                String highPriorityMessage = String.format("Alerte critique détectée : %s (Type: %s, Gravité: %s, Valeur: %.2f %s)",
                        alerte.getCapteur().getNomComplet(),
                        alerte.getTypePanne(),
                        alerte.getNiveauGravite(),
                        alerte.getValeurDeclenchement(),
                        alerte.getCapteur().getUniteMesure());
                notificationService.createNotification(
                        admin,
                        Notification.NotificationType.HIGH_PRIORITY_ALERT,
                        highPriorityMessage,
                        null,
                        null,
                        savedAlerte
                );
            }
        }
    }

    public boolean existeAlerteNonResoluePourCapteur(Capteur capteur, Alerte.NiveauGravite niveau) {
        return alerteRepository.existsByCapteurAndNiveauGraviteAndEstResoluFalse(capteur, niveau);
    }

    @Transactional
    public void resoudreAlertesPourCapteur(Capteur capteur) {
        List<Alerte> alertes = alerteRepository.findByCapteurAndEstResoluFalse(capteur);
        alertes.forEach(alerte -> {
            alerte.setEstResolu(true);
        });
        alerteRepository.saveAll(alertes);
    }

    @Transactional
    public Optional<Alerte> marquerCommeResolue(Long idAlerte) {
        return alerteRepository.findById(idAlerte)
                .map(alerte -> {
                    alerte.setEstResolu(true);
                    return alerteRepository.save(alerte);
                });
    }

    public List<Alerte> getAllAlertesAvecCapteurs() {
        return alerteRepository.findAll();
    }

    public List<Alerte> getAlertesParType(Alerte.TypePanne typePanne) {
        return alerteRepository.findAll()
                .stream()
                .filter(alerte -> alerte.getTypePanne() == typePanne)
                .collect(Collectors.toList());
    }

    public List<Alerte> getAlertesParGravite(Alerte.NiveauGravite niveauGravite) {
        return alerteRepository.findAll()
                .stream()
                .filter(alerte -> alerte.getNiveauGravite() == niveauGravite)
                .collect(Collectors.toList());
    }

    public List<Alerte> getAlertesParResolution(boolean estResolu) {
        return alerteRepository.findAll()
                .stream()
                .filter(alerte -> alerte.getEstResolu() == estResolu)
                .collect(Collectors.toList());
    }

    public List<Alerte> searchAlertes(Alerte.TypePanne typePanne,
                                      Alerte.NiveauGravite niveauGravite,
                                      Boolean estResolu) {
        return alerteRepository.findAll()
                .stream()
                .filter(alerte -> (typePanne == null || alerte.getTypePanne() == typePanne))
                .filter(alerte -> (niveauGravite == null || alerte.getNiveauGravite() == niveauGravite))
                .filter(alerte -> (estResolu == null || alerte.getEstResolu() == estResolu))
                .collect(Collectors.toList());
    }

    public Map<Alerte.TypePanne, Long> countAlertesByType() {
        List<Alerte> alertes = alerteRepository.findAll();
        Map<Alerte.TypePanne, Long> stats = new HashMap<>();

        for (Alerte.TypePanne type : Alerte.TypePanne.values()) {
            long count = alertes.stream()
                    .filter(alerte -> alerte.getTypePanne() == type)
                    .count();
            stats.put(type, count);
        }

        return stats;
    }

    public Map<Alerte.NiveauGravite, Long> countAlertesByGravite() {
        List<Alerte> alertes = alerteRepository.findAll();
        Map<Alerte.NiveauGravite, Long> stats = new HashMap<>();

        for (Alerte.NiveauGravite niveau : Alerte.NiveauGravite.values()) {
            long count = alertes.stream()
                    .filter(alerte -> alerte.getNiveauGravite() == niveau)
                    .count();
            stats.put(niveau, count);
        }

        return stats;
    }

    public double averageResolutionTime() {
        List<Alerte> alertesResolues = alerteRepository.findByEstResolu(true);

        if (alertesResolues.isEmpty()) {
            return 0.0;
        }

        double totalHours = alertesResolues.stream()
                .mapToDouble(alerte -> {
                    LocalDateTime debut = alerte.getDateAlerte();
                    LocalDateTime fin = alerte.getInterventions().stream()
                            .map(intervention -> intervention.getDateFin())
                            .filter(date -> date != null)
                            .findFirst()
                            .orElse(LocalDateTime.now());
                    return Duration.between(debut, fin).toHours();
                })
                .sum();

        return totalHours / alertesResolues.size();
    }

    public double resolutionRate() {
        long totalAlertes = alerteRepository.count();
        if (totalAlertes == 0) {
            return 0.0;
        }

        long alertesResolues = alerteRepository.countByEstResolu(true);
        return (alertesResolues * 100.0) / totalAlertes;
    }

    public long countAlertesBetween(LocalDateTime debut, LocalDateTime fin) {
        return alerteRepository.countByDateAlerteBetween(debut, fin);
    }

    public Alerte saveAlerte(Alerte alerte) {
        return  alerteRepository.save(alerte);
    }

}