package com.example.cckback.dto;

public class PredictionRequest {
    private String specialite;
    private String typeIntervention;
    private String priorite;

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getPriorite() {
        return priorite;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public String getTypeIntervention() {
        return typeIntervention;
    }
    public void setTypeIntervention(String typeIntervention) {
        this.typeIntervention = typeIntervention;
    }
}
