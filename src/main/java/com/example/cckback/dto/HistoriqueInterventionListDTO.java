package com.example.cckback.dto;

import com.example.cckback.Entity.Statut;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class HistoriqueInterventionListDTO {
    private Long idHistoriqueIntervention;
    private Long interventionId;
    private List<String> techniciens; // Noms des techniciens
    private String rapport;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateAction;
    private Statut statut;

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }
    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }
    public Statut getStatut() {
        return statut;
    }
    public void setStatut(Statut statut) {
        this.statut = statut;
    }
    public Long getIdHistoriqueIntervention() {
        return idHistoriqueIntervention;
    }
    public void setIdHistoriqueIntervention(Long idHistoriqueIntervention) {
        this.idHistoriqueIntervention = idHistoriqueIntervention;
    }
    public Long getInterventionId() {
        return interventionId;
    }
    public void setInterventionId(Long interventionId) {
        this.interventionId = interventionId;
    }
    public List<String> getTechniciens() {
        return techniciens;
    }
    public void setTechniciens(List<String> techniciens) {
        this.techniciens = techniciens;
    }
    public String getRapport() {
        return rapport;

    }
    public void setRapport(String rapport) {
        this.rapport = rapport;
    }

// Constructeurs, getters et setters
    // (Lombok génère déjà les getters/setters avec les annotations)
}