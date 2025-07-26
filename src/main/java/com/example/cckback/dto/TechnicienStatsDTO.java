package com.example.cckback.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter

@AllArgsConstructor
@Builder
public class TechnicienStatsDTO {

    private Long idUser;
    private String nom;
    private String prenom;
    private String specialite;
    private int nbInterventions;
    private String dureeTotale;
    private String dureeMoyenne;
    private String tauxReussite;
    private Map<String, Integer> statsParPriorite;
    private Map<String, Integer> statsParType;

    // 👇 constructeur vide requis pour new TechnicienStatsDTO()
    public TechnicienStatsDTO() {
    }

    // 👇 constructeur avec un seul paramètre
    public TechnicienStatsDTO(Long idUser) {
        this.idUser = idUser;
    }

    // 👇 tous les getters/setters
    public Long getIdUser() {
        return idUser;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
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

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public int getNbInterventions() {
        return nbInterventions;
    }

    public void setNbInterventions(int nbInterventions) {
        this.nbInterventions = nbInterventions;
    }

    public String getDureeTotale() {
        return dureeTotale;
    }

    public void setDureeTotale(String dureeTotale) {
        this.dureeTotale = dureeTotale;
    }

    public String getDureeMoyenne() {
        return dureeMoyenne;
    }

    public void setDureeMoyenne(String dureeMoyenne) {
        this.dureeMoyenne = dureeMoyenne;
    }

    public String getTauxReussite() {
        return tauxReussite;
    }

    public void setTauxReussite(String tauxReussite) {
        this.tauxReussite = tauxReussite;
    }

    public Map<String, Integer> getStatsParPriorite() {
        return statsParPriorite;
    }

    public void setStatsParPriorite(Map<String, Integer> statsParPriorite) {
        this.statsParPriorite = statsParPriorite;
    }

    public Map<String, Integer> getStatsParType() {
        return statsParType;
    }

    public void setStatsParType(Map<String, Integer> statsParType) {
        this.statsParType = statsParType;
    }
}

