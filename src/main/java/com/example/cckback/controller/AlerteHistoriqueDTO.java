package com.example.cckback.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlerteHistoriqueDTO {
    private Long idAlerte;
    private String typePanne;
    private String niveauGravite;
    private Double valeurDeclenchement;
    private String typeCapteur;
    private String emplacement;
    private String description;
    private String rapport;
    private String details;
    private Integer satisfaction;

    public void setValeurDeclenchement(Double valeurDeclenchement) {
        this.valeurDeclenchement = valeurDeclenchement;
    }
    public Double getValeurDeclenchement() {
        return valeurDeclenchement;

    }
    public void setTypePanne(String typePanne) {
        this.typePanne = typePanne;
    }
    public String getTypePanne() {
        return typePanne;
    }
    public void setNiveauGravite(String niveauGravite) {
        this.niveauGravite = niveauGravite;
    }
    public String getNiveauGravite() {
        return niveauGravite;
    }
    public void setValeurCapteur(Double valeurCapteur) {
        this.valeurDeclenchement=valeurCapteur;

    }
    public Double getValeurCapteur() {
        return valeurDeclenchement;
    }
    public void setTypeCapteur(String typeCapteur) {
        this.typeCapteur = typeCapteur;
    }
    public String getTypeCapteur() {
        return typeCapteur;
    }
    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }
    public String getEmplacement() {
        return emplacement;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public void setRapport(String rapport) {
        this.rapport = rapport;
    }
    public String getRapport() {
        return rapport;
    }
    public void setDetails(String details) {
        this.details = details;
    }
    public String getDetails() {
        return details;
    }
    public void setSatisfaction(Integer satisfaction) {
        this.satisfaction = satisfaction;
    }
    public Integer getSatisfaction() {
        return satisfaction;

    }
public void setIdAlerte(Long idAlerte) {
        this.idAlerte = idAlerte;
}
public Long getIdAlerte() {
        return idAlerte;
}
// Constructeurs, Getters et Setters
}