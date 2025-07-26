package com.example.cckback.dto;

import com.example.cckback.Entity.Statut;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoriqueInterventionDTO {
    private Long interventionId;
    private String description;
    private String rapport;
    private Statut statut;

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getRapport() {
        return rapport;
    }
    public void setRapport(String rapport) {
        this.rapport = rapport;
    }
    public Statut getStatut() {
        return statut;

    }
    public void setStatut(Statut statut) {
        this.statut = statut;

    }
    public Long getInterventionId() {
        return interventionId;

    }
    public void setInterventionId(Long interventionId) {
        this.interventionId = interventionId;

    }

}
