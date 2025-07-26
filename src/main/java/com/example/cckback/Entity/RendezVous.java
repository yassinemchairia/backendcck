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
public class RendezVous {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRendezvous;

    private String description;
    private LocalDateTime dateRendezVous;
    private boolean notificationEnvoyee;

    @ManyToOne
    private Administrateur administrateur;
    @ManyToMany
    @JoinTable(
            name = "rendezvous_technicien",
            joinColumns = @JoinColumn(name = "rendezvous_id"),
            inverseJoinColumns = @JoinColumn(name = "technicien_id")
    )
    private List<Technicien> techniciens;

    public void setDescription(String description) {
        this.description = description;
    }
    public void setDateRendezVous(LocalDateTime dateRendezVous) {
        this.dateRendezVous = dateRendezVous;

    }
    public void setNotificationEnvoyee(boolean notificationEnvoyee) {
        this.notificationEnvoyee = notificationEnvoyee;
    }

    public void setAdministrateur(Administrateur administrateur) {
        this.administrateur = administrateur;
    }
    public void setTechniciens(List<Technicien> techniciens) {
        this.techniciens = techniciens;
    }

    public String getDescription() {
        return description;
    }

    public Administrateur getAdministrateur() {
        return administrateur;
    }

    public List<Technicien> getTechniciens() {
        return techniciens;
    }

    public LocalDateTime getDateRendezVous() {
        return dateRendezVous;
    }

    public Long getIdRendezvous() {
        return idRendezvous;
    }

    public void setIdRendezvous(Long idRendezvous) {
        this.idRendezvous = idRendezvous;
    }

    public boolean isNotificationEnvoyee() {
        return notificationEnvoyee;
    }



}

