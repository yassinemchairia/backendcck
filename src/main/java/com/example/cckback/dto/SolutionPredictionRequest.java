package com.example.cckback.dto;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class SolutionPredictionRequest {
    private String typePanne;
    private String niveauGravite;
    private Double valeurDeclenchement;
    private String typeCapteur;
    private String emplacement;
    private String description;

    // Getters et Setters
    public String getTypePanne() { return typePanne; }
    public void setTypePanne(String typePanne) { this.typePanne = typePanne; }
    public String getNiveauGravite() { return niveauGravite; }
    public void setNiveauGravite(String niveauGravite) { this.niveauGravite = niveauGravite; }
    public Double getValeurDeclenchement() { return valeurDeclenchement; }
    public void setValeurDeclenchement(Double valeurDeclenchement) { this.valeurDeclenchement = valeurDeclenchement; }
    public String getTypeCapteur() { return typeCapteur; }
    public void setTypeCapteur(String typeCapteur) { this.typeCapteur = typeCapteur; }
    public String getEmplacement() { return emplacement; }
    public void setEmplacement(String emplacement) { this.emplacement = emplacement; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}