package com.example.cckback.service;


import com.example.cckback.Entity.*;
import com.example.cckback.Repository.InterventionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatistiqueService {

    @Autowired
    private InterventionRepository interventionRepository;

    // Statistiques globales pour les interventions préventives
    public Map<String, Object> getStatistiquesPrevention() {
        Map<String, Object> stats = new HashMap<>();

        // Récupérer toutes les interventions préventives
        List<Intervention> interventionsPreventives = interventionRepository.findByTypeIntervention(TypeIntervention.PREVENTIVE);

        // 1. Nombre total d'interventions préventives
        long totalInterventions = interventionsPreventives.size();
        stats.put("totalInterventionsPreventives", totalInterventions);

        // 2. Répartition par type de panne
        Map<Alerte.TypePanne, Long> repartitionParTypePanne = interventionsPreventives.stream()
                .filter(intervention -> intervention.getAlerte() != null)
                .collect(Collectors.groupingBy(
                        intervention -> intervention.getAlerte().getTypePanne(),
                        Collectors.counting()
                ));
        stats.put("repartitionParTypePanne", repartitionParTypePanne);

        // 3. Répartition par spécialité des techniciens
        Map<Specialite, Long> repartitionParSpecialite = interventionsPreventives.stream()
                .flatMap(intervention -> intervention.getTechniciens().stream())
                .collect(Collectors.groupingBy(
                        Technicien::getSpecialite,
                        Collectors.counting()
                ));
        stats.put("repartitionParSpecialite", repartitionParSpecialite);

        // 4. Taux de résolution des alertes
        long alertesResolues = interventionsPreventives.stream()
                .filter(intervention -> intervention.getAlerte() != null && intervention.getAlerte().getEstResolu())
                .count();
        double tauxResolution = totalInterventions > 0 ? (alertesResolues * 100.0 / totalInterventions) : 0;
        stats.put("tauxResolution", tauxResolution);

        // 5. Durée moyenne des interventions
        double dureeMoyenne = interventionsPreventives.stream()
                .filter(intervention -> intervention.getDateDebut() != null && intervention.getDateFin() != null)
                .mapToLong(intervention -> Duration.between(intervention.getDateDebut(), intervention.getDateFin()).toHours())
                .average()
                .orElse(0.0);
        stats.put("dureeMoyenneHeures", dureeMoyenne);

        // 6. Coût moyen des interventions
        double coutMoyen = interventionsPreventives.stream()
                .filter(intervention -> intervention.getRapport() != null)
                .mapToDouble(intervention -> intervention.getRapport().getCoutIntervention())
                .average()
                .orElse(0.0);
        stats.put("coutMoyen", coutMoyen);

        return stats;
    }
}
