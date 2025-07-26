package com.example.cckback.dto;

import java.time.LocalDateTime;

public class PredictionDataDTO {
    private Long id;

    private String specialite;
    private String typeIntervention;
    private String priorite;
    private int technicienId;
    private LocalDateTime dateDebut;
    private int dureeEnHeures;
    private int nombreInterventionsPassees;
    private int nombreTechniciens;
    private String prioriteAttribution;
    // Constructeur vide
    public PredictionDataDTO() {
    }

    // Constructeur avec paramètres
    public PredictionDataDTO(Long id, int technicienId, String specialite, String typeIntervention, String priorite,
                                 LocalDateTime dateDebut, int dureeEnHeures, int nombreInterventionsPassees,
                                 int nombreTechniciens, String prioriteAttribution) {
        this.id = id;
        this.technicienId = technicienId;
        this.specialite = specialite;
        this.typeIntervention = typeIntervention;
        this.priorite = priorite;
        this.dateDebut = dateDebut;
        this.dureeEnHeures = dureeEnHeures;
        this.nombreInterventionsPassees = nombreInterventionsPassees;
        this.nombreTechniciens = nombreTechniciens;
        this.prioriteAttribution = prioriteAttribution;
    }


    // Getters et Setters
    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getTypeIntervention() {
        return typeIntervention;
    }

    public void setTypeIntervention(String typeIntervention) {
        this.typeIntervention = typeIntervention;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public int getTechnicienId() {
        return technicienId;
    }

    public void setTechnicienId(int technicienId) {
        this.technicienId = technicienId;
    }
    public LocalDateTime getDateDebut() {
        return dateDebut;

    }
    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }
    public int getDureeEnHeures() {
        return dureeEnHeures;
    }
    public void setDureeEnHeures(int dureeEnHeures) {
        this.dureeEnHeures = dureeEnHeures;
    }
    public int getNombreInterventionsPassees() {
        return nombreInterventionsPassees;
    }
    public void setNombreInterventionsPassees(int nombreInterventionsPassees) {
        this.nombreInterventionsPassees = nombreInterventionsPassees;
    }
    public int getNombreTechniciens() {
        return nombreTechniciens;
    }
    public void setNombreTechniciens(int nombreTechniciens) {
        this.nombreTechniciens = nombreTechniciens;

    }
    public String getPrioriteAttribution() {
        return prioriteAttribution;
    }
    public void setPrioriteAttribution(String prioriteAttribution) {
        this.prioriteAttribution = prioriteAttribution;

    }

}
