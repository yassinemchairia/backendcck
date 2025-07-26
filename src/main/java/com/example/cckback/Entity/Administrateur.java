package com.example.cckback.Entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Administrateur extends Utilisateur {


    private String departement; // Facultatif



    // Le setter pour departement
    public void setDepartement(String departement) {
        this.departement = departement;
    }

    // Le getter pour departement
    public String getDepartement() {
        return departement;
    }

}
