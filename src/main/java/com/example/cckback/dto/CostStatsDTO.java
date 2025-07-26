package com.example.cckback.dto;

import com.example.cckback.Entity.TypeIntervention;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CostStatsDTO {
    private Long interventionId;
    private TypeIntervention typeIntervention;
    private LocalDate dateIntervention;
    private Double cost;
    private Long durationHours;

    public TypeIntervention getTypeIntervention() {
        return typeIntervention;
    }
    public void setTypeIntervention(TypeIntervention typeIntervention) {
        this.typeIntervention = typeIntervention;
    }
    public LocalDate getDateIntervention() {
        return dateIntervention;
    }
    public void setDateIntervention(LocalDate dateIntervention) {
        this.dateIntervention = dateIntervention;
    }
    public Double getCost() {
        return cost;
    }
    public void setCost(Double cost) {
        this.cost = cost;
    }
    public Long getDurationHours() {
        return durationHours;
    }
    public void setDurationHours(Long durationHours) {
        this.durationHours = durationHours;
    }

    public Long getInterventionId() {
        return interventionId;
    }
    public void setInterventionId(Long interventionId) {
        this.interventionId = interventionId;
    }
}