package com.example.cckback.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RendezVousStatsDTO {
    private Long technicienId;
    private String nomComplet;
    private Long nombreRdvsTotal;
    private Long nombreRdvsPresents;
    private Double tauxParticipation;

    public void setTechnicienId(Long technicienId) {
        this.technicienId = technicienId;
    }
    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }
    public void setNombreRdvsTotal(Long nombreRdvsTotal) {
        this.nombreRdvsTotal = nombreRdvsTotal;
    }
    public void setNombreRdvsPresents(Long nombreRdvsPresents) {
        this.nombreRdvsPresents = nombreRdvsPresents;
    }
    public void setTauxParticipation(Double tauxParticipation) {
        this.tauxParticipation = tauxParticipation;
    }

    public Long getTechnicienId() {
        return technicienId;
    }
    public String getNomComplet() {
        return nomComplet;
    }
    public Long getNombreRdvsTotal() {
        return nombreRdvsTotal;
    }
    public Long getNombreRdvsPresents() {
        return nombreRdvsPresents;
    }
    public Double getTauxParticipation() {
        return tauxParticipation;
    }

}