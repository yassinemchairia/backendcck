package com.example.cckback.dto;

import com.example.cckback.Entity.Statut;
import com.example.cckback.Entity.TypeIntervention;
import com.example.cckback.Entity.PrioriteIntervention;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class InterventionDTO {
    private Long idInterv;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateDebut;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateFin;
    private Statut statut;
    private TypeIntervention typeIntervention;
    private PrioriteIntervention priorite;

    // Constructeur
    public InterventionDTO(Long idInterv, LocalDateTime dateDebut, LocalDateTime dateFin,
                           Statut statut, TypeIntervention typeIntervention, PrioriteIntervention priorite) {
        this.idInterv = idInterv;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.typeIntervention = typeIntervention;
        this.priorite = priorite;

    }

    // Getters et setters
    public Long getIdInterv() {
        return idInterv;
    }

    public void setIdInterv(Long idInterv) {
        this.idInterv = idInterv;
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
}