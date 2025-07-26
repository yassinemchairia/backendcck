package com.example.cckback.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class StatistiquesPreventionDTO {
    private long interventionsPreventives;
    private long interventionsCorrectives;
    private double ratioPreventifCorrectif;
    private double reductionAlertesApresPrevention; // en pourcentage

    public StatistiquesPreventionDTO(long interventionsPreventives,
                                     long interventionsCorrectives,
                                     double ratioPreventifCorrectif,
                                     double reductionAlertesApresPrevention) {
        this.interventionsPreventives = interventionsPreventives;
        this.interventionsCorrectives = interventionsCorrectives;
        this.ratioPreventifCorrectif = ratioPreventifCorrectif;
        this.reductionAlertesApresPrevention = reductionAlertesApresPrevention;
    }
}
