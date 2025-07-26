package com.example.cckback.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RapportIntervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "rapport", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Intervention intervention;

    private String details;
    private double coutIntervention;

    private int satisfaction; // Note entre 1 et 5

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }
    public void setDetails(String details) {
        this.details = details;
    }

    public void setCoutIntervention(double coutIntervention) {
        this.coutIntervention = coutIntervention;
    }

    public void setSatisfaction(int satisfaction) {
        this.satisfaction = satisfaction;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public double getCoutIntervention() {
        return coutIntervention;
    }

    public String getDetails() {
        return details;
    }

    public Intervention getIntervention() {
        return intervention;
    }
}
