package com.example.cckback.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // ✅ Ajout pour éviter les problèmes de sérialisation

public class CalendrierDisponibilite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCalendrierDisponibilite;

    @ManyToOne
    @JoinColumn(name = "technicien_id_user")
    @JsonIgnoreProperties("disponibilites")
    private Technicien technicien;

    private LocalDate date;
    private boolean disponible;

    public void setTechnicien(Technicien technicien) {
        this.technicien = technicien;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    public boolean isDisponible() {  // Méthode pour vérifier la disponibilité
        return disponible;
    }

    public Technicien getTechnicien() {
        return technicien;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setIdCalendrierDisponibilite(Long idCalendrierDisponibilite) {
        this.idCalendrierDisponibilite = idCalendrierDisponibilite;
    }

    public Long getIdCalendrierDisponibilite() {
        return idCalendrierDisponibilite;
    }
}

