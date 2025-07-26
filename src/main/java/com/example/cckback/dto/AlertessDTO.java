package com.example.cckback.dto;

public class AlertessDTO {
    private Long idAlerte;
    private String typePanne;
    private String niveauGravite;
    private Double valeurDeclenchement;
    private String typeCapteur;
    private String emplacement;
    private String description;
    private String solution;
    private Integer satisfaction;

    // Constructeurs, getters, setters
    public AlertessDTO() {}
    public AlertessDTO(Long idAlerte, String typePanne, String niveauGravite, Double valeurDeclenchement,
                     String typeCapteur, String emplacement, String description, String solution, Integer satisfaction) {
        this.idAlerte = idAlerte;
        this.typePanne = typePanne;
        this.niveauGravite = niveauGravite;
        this.valeurDeclenchement = valeurDeclenchement;
        this.typeCapteur = typeCapteur;
        this.emplacement = emplacement;
        this.description = description;
        this.solution = solution;
        this.satisfaction = satisfaction;
    }
    public Long getIdAlerte() {
        return idAlerte;
    }
    public void setIdAlerte(Long idAlerte) {
        this.idAlerte = idAlerte;
    }
    public String getTypePanne() {
        return typePanne;
    }
    public void setTypePanne(String typePanne) {
        this.typePanne = typePanne;
    }
    public String getNiveauGravite() {
        return niveauGravite;
    }
    public void setNiveauGravite(String niveauGravite) {
        this.niveauGravite = niveauGravite;
    }
    public Double getValeurDeclenchement() {
        return valeurDeclenchement;
    }

    public String getTypeCapteur() {
        return typeCapteur;
    }
    public void setTypeCapteur(String typeCapteur) {
        this.typeCapteur = typeCapteur;
    }
    public String getEmplacement() {
        return emplacement;
    }
    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getSolution() {
        return solution;
    }
    public void setSolution(String solution) {
        this.solution = solution;
    }
    public Integer getSatisfaction() {
        return satisfaction;
    }
    public void setSatisfaction(Integer satisfaction) {
        this.satisfaction = satisfaction;
    }

    public void setValeurDeclenchement(double valeurDeclenchement) {
        this.valeurDeclenchement = valeurDeclenchement;
    }
}
