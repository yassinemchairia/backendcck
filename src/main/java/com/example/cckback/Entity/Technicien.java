package com.example.cckback.Entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Technicien extends Utilisateur {


    @Enumerated(EnumType.STRING)
    @JsonProperty("specialite")
    private Specialite specialite; // Électricité, Climatisation, etc.
    @JsonProperty("numeroTelephone")
    private String numeroTelephone;
    @Transient
    @JsonProperty("dateDisponibilite")
    private LocalDate dateDisponibilite;

    @ManyToMany(mappedBy = "techniciens")
    @JsonBackReference
    private List<Intervention> interventions = new ArrayList<>();

    @ManyToMany(mappedBy = "techniciens")
    private List<RendezVous> rendezVous;

    @ManyToMany(mappedBy = "techniciens")
    private List<HistoriqueIntervention> historiqueInterventions;

    @OneToMany(mappedBy = "technicien")
    private List<CalendrierDisponibilite> disponibilites;

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    public void setInterventions(List<Intervention> interventions) {
        this.interventions = interventions;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    @Override
    public Long getIdUser() {
        return super.getIdUser();
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }
    public LocalDate getDateDisponibilite() {
        return dateDisponibilite;
    }

    public void setDateDisponibilite(LocalDate dateDisponibilite) {
        this.dateDisponibilite = dateDisponibilite;
    }
public List<Intervention> getInterventions() {
        return interventions;
}

}