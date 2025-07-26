package com.example.cckback.dto;

import com.example.cckback.Entity.Specialite;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AutoPlanificationRequest {
    private Long adminId;
    private String description;
    private LocalDate dateSouhaitee; // Date souhaitée
    private LocalDate dateLimite; // Date limite (optionnelle)
    private Specialite specialiteRequise; // Spécialité nécessaire
    private int nombreTechniciensRequis; // Nombre de techniciens nécessaires

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateSouhaitee(LocalDate dateSouhaitee) {
        this.dateSouhaitee = dateSouhaitee;
    }

    public void setDateLimite(LocalDate dateLimite) {
        this.dateLimite = dateLimite;
    }

    public void setSpecialiteRequise(Specialite specialiteRequise) {
        this.specialiteRequise = specialiteRequise;
    }

    public void setNombreTechniciensRequis(int nombreTechniciensRequis) {
        this.nombreTechniciensRequis = nombreTechniciensRequis;
    }

    public Specialite getSpecialiteRequise() {
        return specialiteRequise;
    }
    public int getNombreTechniciensRequis() {
        return nombreTechniciensRequis;
    }
    public LocalDate getDateSouhaitee() {
        return dateSouhaitee;
    }
    public LocalDate getDateLimite() {
        return dateLimite;
    }
    public Long getAdminId() {
        return adminId;
    }
    public String getDescription() {
        return description;
    }

}