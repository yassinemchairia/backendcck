
        package com.example.cckback.dto;

import com.example.cckback.Entity.Statut;
import com.example.cckback.Entity.TypeIntervention;
import com.example.cckback.Entity.PrioriteIntervention;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class Intervention1DTO {
    private Long idInterv;
    private Long alerteId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateDebut;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateFin;
    private Statut statut;
    private TypeIntervention typeIntervention;
    private PrioriteIntervention priorite;
    private String solution;
    private Integer satisfaction;

    // Constructeur
    public Intervention1DTO(Long idInterv, Long alerteId, LocalDateTime dateDebut, LocalDateTime dateFin,
                           Statut statut, TypeIntervention typeIntervention, PrioriteIntervention priorite,
                           String solution, Integer satisfaction) {
        this.idInterv = idInterv;
        this.alerteId = alerteId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.typeIntervention = typeIntervention;
        this.priorite = priorite;
        this.solution = solution;
        this.satisfaction = satisfaction;
    }

    // Getters et setters
    public Long getIdInterv() {
        return idInterv;
    }

    public void setIdInterv(Long idInterv) {
        this.idInterv = idInterv;
    }

    public Long getAlerteId() {
        return alerteId;
    }

    public void setAlerteId(Long alerteId) {
        this.alerteId = alerteId;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public TypeIntervention getTypeIntervention() {
        return typeIntervention;
    }

    public void setTypeIntervention(TypeIntervention typeIntervention) {
        this.typeIntervention = typeIntervention;
    }

    public PrioriteIntervention getPriorite() {
        return priorite;
    }

    public void setPriorite(PrioriteIntervention priorite) {
        this.priorite = priorite;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public Integer getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(Integer satisfaction) {
        this.satisfaction = satisfaction;
    }


}
