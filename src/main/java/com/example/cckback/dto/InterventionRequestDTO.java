package com.example.cckback.dto;

import com.example.cckback.Entity.PrioriteIntervention;
import com.example.cckback.Entity.TypeIntervention;

import java.util.List;

public class InterventionRequestDTO {
    private Long idAlerte;
    private List<Long> technicienIds;
    private PrioriteIntervention priorite;
    private TypeIntervention typeIntervention;
    private String dateDebut;
    // Getters and setters
    public Long getIdAlerte() {
        return idAlerte;
    }

    public void setIdAlerte(Long idAlerte) {
        this.idAlerte = idAlerte;
    }

    public List<Long> getTechnicienIds() {
        return technicienIds;
    }

    public void setTechnicienIds(List<Long> technicienIds) {
        this.technicienIds = technicienIds;
    }
    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }
    public PrioriteIntervention getPriorite() {
        return priorite;
    }

    public void setPriorite(PrioriteIntervention priorite) {
        this.priorite = priorite;
    }

    public TypeIntervention getTypeIntervention() {
        return typeIntervention;
    }

    public void setTypeIntervention(TypeIntervention typeIntervention) {
        this.typeIntervention = typeIntervention;
    }
}