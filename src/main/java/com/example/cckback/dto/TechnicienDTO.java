package com.example.cckback.dto;

import com.example.cckback.Entity.Specialite;

public class TechnicienDTO {
    private Long idUser;
    private String nom;
    private String prenom;
    private Specialite specialite;

    // Constructeurs, getters et setters
    public TechnicienDTO() {}

    public TechnicienDTO(Long idUser, String nom, String prenom, Specialite specialite) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
    }

    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;

    }
    public Long getIdUser() {
        return idUser;
    }
    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public Specialite getSpecialite() {
        return specialite;
    }
    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

// Getters et setters...
}