package com.example.cckback.dto;

import com.example.cckback.Entity.Specialite;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class TechniciensDTO {
    @JsonProperty("idUser")
    private Long idUser;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private String role;

    @JsonProperty("valide")
    private boolean valide;

    @JsonProperty("specialite")
    private Specialite specialite;

    @JsonProperty("numeroTelephone")
    private String numeroTelephone;

    @JsonProperty("dateDisponibilite")
    private LocalDate dateDisponibilite;

    public TechniciensDTO(Long idUser, String email, String role, boolean valide, Specialite specialite, String numeroTelephone, LocalDate dateDisponibilite) {
        this.idUser = idUser;
        this.email = email;
        this.role = role;
        this.valide = valide;
        this.specialite = specialite;
        this.numeroTelephone = numeroTelephone;
        this.dateDisponibilite = dateDisponibilite;
    }

    // Getters and setters
    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public LocalDate getDateDisponibilite() {
        return dateDisponibilite;
    }

    public void setDateDisponibilite(LocalDate dateDisponibilite) {
        this.dateDisponibilite = dateDisponibilite;
    }
}