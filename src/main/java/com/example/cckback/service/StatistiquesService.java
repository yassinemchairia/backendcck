package com.example.cckback.service;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.InterventionRepository;
import com.example.cckback.dto.StatistiquesPreventionDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class StatistiquesService {

    private final InterventionRepository interventionRepository;
    private final AlerteService alerteService; // Vous devrez créer ce service

    public StatistiquesService(InterventionRepository interventionRepository, AlerteService alerteService) {
        this.interventionRepository = interventionRepository;
        this.alerteService = alerteService;
    }

    public StatistiquesPreventionDTO getStatistiquesPrevention() {
        // Compter les interventions par type
        long preventives = interventionRepository.countByTypeIntervention(TypeIntervention.PREVENTIVE);
        long correctives = interventionRepository.countByTypeIntervention(TypeIntervention.CORRECTIVE);

        // Calculer le ratio
        double ratio = correctives == 0 ? preventives : (double) preventives / correctives;

        // Calculer la réduction des alertes après prévention
        double reduction = calculerReductionAlertesApresPrevention();

        return new StatistiquesPreventionDTO(preventives, correctives, ratio, reduction);
    }

    private double calculerReductionAlertesApresPrevention() {
        // Période avant (par exemple 3 mois avant les interventions préventives)
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime troisMoisAvant = maintenant.minus(3, ChronoUnit.MONTHS);
        LocalDateTime troisMoisApres = maintenant.plus(3, ChronoUnit.MONTHS);

        // Trouver les interventions préventives dans cette période
        List<Intervention> preventives = interventionRepository.findByTypeAndDateBetween(
                TypeIntervention.PREVENTIVE, troisMoisAvant, maintenant);

        if (preventives.isEmpty()) {
            return 0.0;
        }

        // Compter les alertes avant et après
        long alertesAvant = alerteService.countAlertesBetween(troisMoisAvant, maintenant);
        long alertesApres = alerteService.countAlertesBetween(maintenant, troisMoisApres);

        // Calculer la réduction en pourcentage
        if (alertesAvant == 0) {
            return 0.0;
        }

        return ((double) (alertesAvant - alertesApres) / alertesAvant) * 100;
    }
}