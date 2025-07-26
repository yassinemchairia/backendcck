package com.example.cckback.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Alerte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlerte;

    @ManyToOne
    @JsonBackReference
    private Capteur capteur;

    @Enumerated(EnumType.STRING)
    private TypePanne typePanne;

    @Enumerated(EnumType.STRING)
    private NiveauGravite niveauGravite;

    private LocalDateTime dateAlerte;
    private LocalDateTime dateResolution;
    private String description;

    private boolean estResolu;
    private Double valeurDeclenchement; // Nouveau champ pour stocker la valeur qui a déclenché l'alerte

    @OneToMany(mappedBy = "alerte")
    @JsonIgnore // Prevent recursive serialization

    private List<Intervention> interventions;

    public Double getValeurDeclenclement() {
        return valeurDeclenchement;
    }

    public boolean isEstResolu() {
        return estResolu;
    }


    public enum TypePanne {
        ELECTRICITE, CLIMATISATION, ENVIRONNEMENT
    }

    public enum NiveauGravite {
        BAS_CRITIQUE,BAS,NORMALE, CRITIQUE, HIGH_CRITICAL
    }
    public void setCapteur(Capteur capteur) {
        this.capteur = capteur;
    }

    public void setIdAlerte(Long idAlerte) {
        this.idAlerte = idAlerte;
    }

    public void setInterventions(List<Intervention> interventions) {
        this.interventions = interventions;
    }

    public LocalDateTime getDateResolution() {
        return dateResolution;
    }

    public void setTypePanne(TypePanne typePanne) {
        this.typePanne = typePanne;
    }

    public void setNiveauGravite(NiveauGravite niveauGravite) {
        this.niveauGravite = niveauGravite;
    }

    public Double getValeurDeclenchement() {
        return valeurDeclenchement;
    }

    public void setDateResolution(LocalDateTime dateResolution) {
        this.dateResolution = dateResolution;
    }

    public boolean getEstResolu() {
        return estResolu;
    }

    public String getDescription() {
        return description;
    }

    public void setDateAlerte(LocalDateTime dateAlerte) {
        this.dateAlerte = dateAlerte;
    }
    public LocalDateTime getDateAlerte() {
        return dateAlerte;
    }
    public void setEstResolu(boolean estResolu) {
        this.estResolu = estResolu;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getIdAlerte() {
        return idAlerte;
    }

    public void setValeurDeclenchement(Double valeurDeclenchement) {
        this.valeurDeclenchement = valeurDeclenchement;
    }

    public Capteur getCapteur() {
        return capteur;
    }
    public TypePanne getTypePanne() {
        return typePanne;
    }
    public NiveauGravite getNiveauGravite() {
        return niveauGravite;
    }

    public List<Intervention> getInterventions() {
        return interventions;
    }
    @PrePersist
    public void prePersist() {
        if (dateAlerte == null) {
            dateAlerte = LocalDateTime.now();
        }
    }

    public void resoudre() {
        this.estResolu = true;
        this.dateResolution = LocalDateTime.now();
    }
}

