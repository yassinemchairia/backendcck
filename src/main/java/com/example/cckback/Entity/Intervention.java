package com.example.cckback.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Intervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInterv;

    @ManyToMany
    @JoinTable(
            name = "technicien_intervention",
            joinColumns = @JoinColumn(name = "intervention_id"),
            inverseJoinColumns = @JoinColumn(name = "technicien_id")
    )
    @JsonManagedReference
    private List<Technicien> techniciens = new ArrayList<>(); // Liste de techniciens affectés à cette intervention

    @ManyToOne
    private Alerte alerte;

    @OneToMany(mappedBy = "intervention")
    private List<HistoriqueIntervention> historique;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @JoinColumn(name = "rapport_id", unique = true)
    private RapportIntervention rapport;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private boolean resolvedByAI; // New flag for AI resolution

    @Enumerated(EnumType.STRING)
    private Statut statut; // EN COURS, TERMINÉE




    @Enumerated(EnumType.STRING)
    private TypeIntervention typeIntervention;

    @Enumerated(EnumType.STRING)
    private PrioriteIntervention priorite;

    public Alerte getAlerte() {
        return alerte;
    }

    public List<Technicien> getTechniciens() {
        return techniciens;
    }

    public List<HistoriqueIntervention> getHistorique() {
        return historique;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public void setTechniciens(List<Technicien> techniciens) {
        this.techniciens = techniciens;
    }

    public void setAlerte(Alerte alerte) {
        this.alerte = alerte;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }
    public void setTypeIntervention(TypeIntervention typeIntervention) {
        this.typeIntervention = typeIntervention;
    }
    public void setPriorite(PrioriteIntervention priorite) {
        this.priorite = priorite;
    }

    public TypeIntervention getTypeIntervention() {
        return typeIntervention;
    }
    public PrioriteIntervention getPriorite() {
        return priorite;
    }
    public boolean isResolvedByAI() { return resolvedByAI; }
    public void setResolvedByAI(boolean resolvedByAI) { this.resolvedByAI = resolvedByAI; }
    public void setRapport(RapportIntervention rapport) {
        this.rapport = rapport;
    }

    public RapportIntervention getRapport() {
        return rapport;
    }

    public Long getIdInterv() {
        return idInterv;
    }

    public void setIdInterv(Long idInterv) {
        this.idInterv = idInterv;
    }

    public Statut getStatut() {
        return statut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

}
