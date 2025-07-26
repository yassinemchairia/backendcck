package com.example.cckback.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class HistoriqueIntervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistoriqueIntervention;

    @ManyToOne
    private Intervention intervention;

    @ManyToMany
    @JoinTable(
            name = "historique_techniciens",
            joinColumns = @JoinColumn(name = "historique_id"),
            inverseJoinColumns = @JoinColumn(name = "technicien_id")
    )
    private List<Technicien> techniciens;

    private String rapport; // Description de l’action
    private String description; // Notes sur l'évolution
    private LocalDateTime dateAction;

    @Enumerated(EnumType.STRING)
    private Statut statut; // EN COURS, TERMINÉE

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }

    public void setTechniciens(List<Technicien> techniciens) {
        this.techniciens = techniciens;
    }

    public void setRapport(String rapport) {
        this.rapport = rapport;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }
    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public String getDescription() {
        return description;
    }

    public Intervention getIntervention() {
        return intervention;
    }

    public String getRapport() {
        return rapport;
    }
    public List<Technicien> getTechniciens() {
        return techniciens;
    }
    public Statut getStatut() {
        return statut;
    }

    public Long getIdHistoriqueIntervention() {
        return idHistoriqueIntervention;
    }
    public void setIdHistoriqueIntervention(Long idHistoriqueIntervention) {
        this.idHistoriqueIntervention = idHistoriqueIntervention;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

}

