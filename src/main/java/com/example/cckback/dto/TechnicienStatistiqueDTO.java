package com.example.cckback.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicienStatistiqueDTO {
    private Long technicienId;
    private String nomComplet;
    private Long nombreInterventions;
    private Double satisfactionMoyenne;
    private Double tauxDisponibilite;

    public void setTechnicienId(Long technicienId) {
        this.technicienId = technicienId;
    }
    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }
    public void setNombreInterventions(Long nombreInterventions) {
        this.nombreInterventions = nombreInterventions;

    }

    public void setSatisfactionMoyenne(Double satisfactionMoyenne) {
        this.satisfactionMoyenne = satisfactionMoyenne;
    }


    public void setTauxDisponibilite(Double tauxDisponibilite) {
        this.tauxDisponibilite = tauxDisponibilite;
    }
    public Long getTechnicienId() {
        return technicienId;
    }
    public String getNomComplet() {
        return nomComplet;
    }
    public Long getNombreInterventions() {
        return nombreInterventions;
    }
    public Double getSatisfactionMoyenne() {
        return satisfactionMoyenne;
    }
    public Double getTauxDisponibilite() {
        return tauxDisponibilite;
    }

}
