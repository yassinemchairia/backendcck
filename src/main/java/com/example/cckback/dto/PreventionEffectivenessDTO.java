package com.example.cckback.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreventionEffectivenessDTO {
    private double avgAlertReduction; // Pourcentage moyen de réduction
    private long totalPreventiveInterventions;
    private long effectiveInterventions; // Nombre d'interventions avec réduction > 0%

    public double getEffectivenessRate() {
        return totalPreventiveInterventions > 0 ?
                (effectiveInterventions * 100.0) / totalPreventiveInterventions : 0;
    }
}