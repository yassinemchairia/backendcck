package com.example.cckback.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TechnicienCostDTO {
    private final String technicienName;
    private final double totalCost;
    private final int interventionCount;
    private final double averageCost;

    public TechnicienCostDTO(String technicienName, double totalCost, int interventionCount) {
        this.technicienName = technicienName;
        this.totalCost = totalCost;
        this.interventionCount = interventionCount;
        this.averageCost = interventionCount > 0 ? totalCost / interventionCount : 0;
    }

    public String getTechnicienName() {
        return technicienName;
    }
    public double getTotalCost() {
        return totalCost;
    }
    public int getInterventionCount() {
        return interventionCount;
    }
    public double getAverageCost() {
        return averageCost;
    }

}
