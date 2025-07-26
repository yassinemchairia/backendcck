package com.example.cckback.service;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AdministrateurRepository;
import com.example.cckback.Repository.InterventionRepository;
import com.example.cckback.Repository.RapportInterventionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RapportInterventionService {

    @Autowired
    private RapportInterventionRepository rapportInterventionRepository;

    @Autowired
    private InterventionRepository interventionRepository;
    @Autowired
    private AdministrateurRepository administrateurRepository;

    @Autowired
    private NotificationService notificationService;

    public RapportIntervention ajouterRapport(Long interventionId, RapportIntervention rapport) {
        Optional<Intervention> interventionOpt = interventionRepository.findById(interventionId);

        if (interventionOpt.isPresent()) {
            Intervention intervention = interventionOpt.get();

            // Vérifier si le rapport contient bien les données
            if (rapport.getDetails() == null || rapport.getDetails().isEmpty()) {
                throw new RuntimeException("Le champ 'details' est obligatoire !");
            }
            if (rapport.getCoutIntervention() == 0) {
                throw new RuntimeException("Le champ 'coutIntervention' est obligatoire !");
            }
            if (rapport.getSatisfaction() < 1 || rapport.getSatisfaction() > 5) {
                throw new RuntimeException("La satisfaction doit être entre 1 et 5 !");
            }

            // Associer le rapport à l'intervention
            rapport.setIntervention(intervention);
            intervention.setRapport(rapport);
            // Mettre à jour l'intervention
            intervention.setStatut(Statut.TERMINEE); // Changer le statut
            intervention.setDateFin(LocalDateTime.now()); // Mettre la date actuelle

            // Sauvegarde du rapport et mise à jour de l'intervention
            RapportIntervention savedRapport = rapportInterventionRepository.save(rapport);
            interventionRepository.save(intervention);

            // Notify all admins of the completed intervention
            List<Administrateur> admins = administrateurRepository.findAll();
            for (Administrateur admin : admins) {
                String message = String.format("L'intervention #%d pour le capteur %s à %s a été terminée.",
                        intervention.getIdInterv(),
                        intervention.getAlerte().getCapteur().getNomComplet(),
                        intervention.getAlerte().getCapteur().getEmplacement());
                notificationService.createNotification(
                        admin,
                        Notification.NotificationType.INTERVENTION_COMPLETED,
                        message,
                        intervention,
                        null,
                        intervention.getAlerte()
                );
            }

            return savedRapport;
        } else {
            throw new RuntimeException("Intervention avec ID " + interventionId + " introuvable !");
        }
    }
    public List<RapportIntervention> getRapportsByUserId(Long idUser) {
        // Find all interventions associated with the technicien
        List<Intervention> interventions = interventionRepository.findByTechniciens_IdUser(idUser);
        // Extract rapports from interventions
        return interventions.stream()
                .filter(intervention -> intervention.getRapport() != null) // Only include interventions with a report
                .map(Intervention::getRapport)
                .collect(Collectors.toList());
    }

}