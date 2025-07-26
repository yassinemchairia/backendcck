package com.example.cckback.service;
import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AlerteRepository;
import com.example.cckback.Repository.InterventionRepository;
import com.example.cckback.Repository.TechnicienRepository;
import com.example.cckback.dto.InterventionCalendarDTO;
import com.example.cckback.dto.InterventionDTO;
import com.example.cckback.dto.TechnicienDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InterventionService {

    private final InterventionRepository interventionRepository;
    private final TechnicienRepository technicienRepository;
    private final AlerteRepository alerteRepository;
    @Autowired
    private  NotificationService notificationService;


    // Add NotificationService
    public InterventionService(InterventionRepository interventionRepository, TechnicienRepository technicienRepository, AlerteRepository alerteRepository) {
        this.interventionRepository = interventionRepository;
        this.technicienRepository = technicienRepository;
        this.alerteRepository = alerteRepository;

    }

    public Intervention ajouterIntervention(LocalDateTime dateDebut, PrioriteIntervention priorite, TypeIntervention typeIntervention, List<Long> idTechniciens, Long idAlerte) {
        // Vérifier si l'alerte existe
        Optional<Alerte> alerteOptional = alerteRepository.findById(idAlerte);
        if (alerteOptional.isEmpty()) {
            throw new RuntimeException("Alerte introuvable avec l'ID : " + idAlerte);
        }
        Alerte alerte = alerteOptional.get();

        // Vérifier si les techniciens existent
        List<Technicien> techniciens = technicienRepository.findAllById(idTechniciens);
        if (techniciens.isEmpty()) {
            throw new RuntimeException("Aucun technicien trouvé pour les IDs fournis !");
        }

        // Créer l'intervention
        Intervention intervention = new Intervention();
        intervention.setDateDebut(dateDebut);
        intervention.setPriorite(priorite);
        intervention.setTypeIntervention(typeIntervention);
        intervention.setTechniciens(techniciens);
        intervention.setAlerte(alerte);
        intervention.setStatut(Statut.EN_COURS); // Toujours "EN COURS" lors de l'ajout

        // Sauvegarder dans la base de données
// Sauvegarder dans la base de données
        Intervention savedIntervention = interventionRepository.save(intervention);

        // Generate notifications for each technician
        for (Technicien technicien : techniciens) {
            String message = String.format("Vous avez été assigné à l'intervention #%d pour le capteur %s à %s.",
                    savedIntervention.getIdInterv(),
                    alerte.getCapteur().getNomComplet(),
                    alerte.getCapteur().getEmplacement());
            notificationService.createNotification(
                    technicien,
                    Notification.NotificationType.INTERVENTION_ASSIGNED,
                    message,
                    savedIntervention,
                    null,
                    alerte
            );
        }

        return savedIntervention;
    }
    public List<InterventionDTO> getInterventionsByTechnicienId(Long idTechnicien) {
        Technicien technicien = technicienRepository.findById(idTechnicien)
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé avec l'ID : " + idTechnicien));

        return technicien.getInterventions().stream()
                .map(intervention -> new InterventionDTO(
                        intervention.getIdInterv(),
                        intervention.getDateDebut(),
                        intervention.getDateFin(),
                        intervention.getStatut(),
                        intervention.getTypeIntervention(),
                        intervention.getPriorite()
                ))
                .collect(Collectors.toList());
    }
    public List<InterventionCalendarDTO> getInterventionsForCalendar(Long technicienId, LocalDate startDate, LocalDate endDate) {
        Technicien technicien = technicienRepository.findById(technicienId)
                .orElseThrow(() -> new EntityNotFoundException("Technicien non trouvé"));

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return technicien.getInterventions().stream()
                .filter(intervention ->
                        intervention.getDateDebut() != null &&
                                !intervention.getDateDebut().isBefore(startDateTime) &&
                                !intervention.getDateDebut().isAfter(endDateTime))
                .map(intervention -> {
                    String title = "Intervention #" + intervention.getIdInterv() +
                            " - " + intervention.getTypeIntervention();

                    LocalDateTime end = intervention.getDateFin() != null ?
                            intervention.getDateFin() :
                            intervention.getDateDebut().plusHours(1);

                    return new InterventionCalendarDTO(
                            intervention.getIdInterv(),
                            title,
                            intervention.getDateDebut(),
                            end,
                            intervention.getStatut(),
                            intervention.getTypeIntervention(),
                            intervention.getPriorite()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<InterventionCalendarDTO> getInterventionsForCalendarByDate(Long technicienId, LocalDate date) {
        return getInterventionsForCalendar(technicienId, date, date);
    }
    public List<InterventionDTO> getAllInterventions() {
        List<Intervention> interventions = interventionRepository.findAllWithTechniciensAndAlerte();
        return interventions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private InterventionDTO convertToDTO(Intervention intervention) {
        return new InterventionDTO(
                intervention.getIdInterv(),
                intervention.getDateDebut(),
                intervention.getDateFin(),
                intervention.getStatut(),
                intervention.getTypeIntervention(),
                intervention.getPriorite()
        );
    }
    public List<TechnicienDTO> getTechniciensAffectes(Long idInterv) {
        Optional<Intervention> optionalIntervention = interventionRepository.findById(idInterv);

        if (optionalIntervention.isPresent()) {
            Intervention intervention = optionalIntervention.get();
            List<Technicien> techniciens = intervention.getTechniciens();

            if (techniciens == null || techniciens.isEmpty()) {
                // Aucun technicien affecté à cette intervention
                return Collections.emptyList(); // ou bien lancer une exception si tu préfères
            }

            return techniciens.stream()
                    .map(tech -> new TechnicienDTO(
                            tech.getIdUser(),
                            tech.getNom(),
                            tech.getPrenom(),
                            tech.getSpecialite()
                    ))
                    .collect(Collectors.toList());

        } else {
            // Intervention introuvable : tu peux soit renvoyer une liste vide, soit lever une exception personnalisée
            throw new EntityNotFoundException("Intervention avec ID " + idInterv + " non trouvée");
        }
    }
    public Map<TypeIntervention, Long> countInterventionsByType() {
        List<Intervention> interventions = interventionRepository.findAll();
        return interventions.stream()
                .collect(Collectors.groupingBy(
                        Intervention::getTypeIntervention,
                        Collectors.counting()
                ));
    }

    // Nombre d'interventions par statut
    public Map<Statut, Long> countInterventionsByStatus() {
        List<Intervention> interventions = interventionRepository.findAll();
        return interventions.stream()
                .collect(Collectors.groupingBy(
                        Intervention::getStatut,
                        Collectors.counting()
                ));
    }

    // Temps moyen d'intervention en heures
    public double calculateAverageInterventionDuration() {
        List<Intervention> completedInterventions = interventionRepository.findByStatut(Statut.TERMINEE);

        if (completedInterventions.isEmpty()) {
            return 0.0;
        }

        double totalHours = completedInterventions.stream()
                .filter(i -> i.getDateDebut() != null && i.getDateFin() != null)
                .mapToDouble(i -> Duration.between(i.getDateDebut(), i.getDateFin()).toHours())
                .sum();

        return totalHours / completedInterventions.size();
    }

    // Nombre d'interventions par priorité
    public Map<PrioriteIntervention, Long> countInterventionsByPriority() {
        List<Intervention> interventions = interventionRepository.findAll();
        return interventions.stream()
                .collect(Collectors.groupingBy(
                        Intervention::getPriorite,
                        Collectors.counting()
                ));
    }

    // Version alternative avec initialisation complète des enum values
    public Map<TypeIntervention, Long> countInterventionsByTypeComplete() {
        Map<TypeIntervention, Long> stats = new HashMap<>();
        for (TypeIntervention type : TypeIntervention.values()) {
            stats.put(type, interventionRepository.countByTypeIntervention(type));
        }
        return stats;
    }
    public Map<TypeIntervention, Long> countInterventionsByTypeBetweenDates(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Intervention> interventions = interventionRepository
                .findByDateDebutBetween(start, end);

        return interventions.stream()
                .collect(Collectors.groupingBy(
                        Intervention::getTypeIntervention,
                        Collectors.counting()
                ));
    }
    public Intervention saveIntervention(Intervention intervention) {
        return interventionRepository.save(intervention);
    }
}