package com.example.cckback.dto;

import com.example.cckback.Entity.PrioriteIntervention;
import com.example.cckback.Entity.Statut;
import com.example.cckback.Entity.TypeIntervention;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class InterventionCalendarDTO {
    private Long idInterv;
    private String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;  private Statut statut;
    private TypeIntervention typeIntervention;
    private PrioriteIntervention priorite;
    private String color;

    public InterventionCalendarDTO(Long idInterv, String title, LocalDateTime start, LocalDateTime end,
                                   Statut statut, TypeIntervention typeIntervention, PrioriteIntervention priorite) {
        this.idInterv = idInterv;
        this.title = title;
        this.start = start;
        this.end = end;
        this.statut = statut;
        this.typeIntervention = typeIntervention;
        this.priorite = priorite;
        this.color = determineColor(statut, priorite);
    }

    private String determineColor(Statut statut, PrioriteIntervention priorite) {
        if (statut == Statut.TERMINEE) {
            return "#4CAF50"; // Vert pour terminé
        } else {
            switch (priorite) {
                case ELEVEE: return "#F44336"; // Rouge pour haute priorité
                case MOYENNE: return "#FFC107"; // Jaune pour moyenne priorité
                case BASSE: return "#2196F3"; // Bleu pour basse priorité
                default: return "#9E9E9E"; // Gris par défaut
            }
        }
    }

    // Getters et Setters
    public Long getIdInterv() {
        return idInterv;
    }

    public void setIdInterv(Long idInterv) {
        this.idInterv = idInterv;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}