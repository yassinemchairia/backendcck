package com.example.cckback.service;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.HistoriqueInterventionRepository;
import com.example.cckback.Repository.InterventionRepository;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.dto.HistoriqueInterventionListDTO;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistoriqueInterventionService {

    private static final Logger logger = LoggerFactory.getLogger(HistoriqueInterventionService.class);

    @Autowired
    private HistoriqueInterventionRepository historiqueInterventionRepository;

    @Autowired
    private InterventionRepository interventionRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public HistoriqueIntervention ajouterHistorique(
            Long interventionId,
            String description,
            String rapport,
            Statut statut) {

        Optional<Intervention> interventionOpt = interventionRepository.findById(interventionId);

        if (interventionOpt.isEmpty()) {
            throw new RuntimeException("Intervention avec ID " + interventionId + " introuvable !");
        }

        Intervention intervention = interventionOpt.get();

        // Validation: vérifier si l'intervention est déjà terminée
        if (intervention.getStatut() == Statut.TERMINEE) {
            throw new IllegalStateException("Impossible de modifier une intervention déjà terminée");
        }

        // Création de la nouvelle entrée historique
        HistoriqueIntervention historique = new HistoriqueIntervention();
        historique.setIntervention(intervention);
        historique.setTechniciens(new ArrayList<>(intervention.getTechniciens()));
        historique.setRapport(rapport);
        historique.setStatut(statut);
        historique.setDescription(description);
        historique.setDateAction(LocalDateTime.now());

        // Journalisation
        logger.info("Ajout historique intervention ID {} - Statut: {} - Description: {}",
                interventionId, statut, description);

        return historiqueInterventionRepository.save(historique);
    }

    @Transactional
    public HistoriqueIntervention ajouterMiseAJour(
            Long interventionId,
            String description,
            String rapport) {

        return ajouterHistorique(
                interventionId,
                description,
                rapport,
                Statut.EN_COURS
        );
    }

    public List<HistoriqueInterventionListDTO> getHistoriquesParIntervention(Long idInterv) {
        List<HistoriqueIntervention> historiques = historiqueInterventionRepository.findByIntervention_IdInterv(idInterv);

        return historiques.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<HistoriqueInterventionListDTO> searchHistoriquesByDescriptionOrRapport(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Le terme de recherche ne peut pas être vide");
        }

        logger.info("Searching historiques with term: {}", searchTerm);
        List<HistoriqueIntervention> historiques = historiqueInterventionRepository.findByDescriptionOrRapportContaining(searchTerm);

        return historiques.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private HistoriqueInterventionListDTO convertToDTO(HistoriqueIntervention historique) {
        HistoriqueInterventionListDTO dto = new HistoriqueInterventionListDTO();
        dto.setIdHistoriqueIntervention(historique.getIdHistoriqueIntervention());
        dto.setInterventionId(historique.getIntervention().getIdInterv());

        // Convertir la liste des techniciens en noms
        List<String> nomsTechniciens = historique.getTechniciens().stream()
                .map(t -> t.getNom() + " " + t.getPrenom())
                .collect(Collectors.toList());
        dto.setTechniciens(nomsTechniciens);

        dto.setRapport(historique.getRapport());
        dto.setDescription(historique.getDescription());
        dto.setDateAction(historique.getDateAction());
        dto.setStatut(historique.getStatut());

        return dto;
    }
}