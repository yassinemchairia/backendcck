package com.example.cckback.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Capteur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCapt;

    private String ipAdresse;
    private String emplacement;
    @Enumerated(EnumType.STRING)
    private Departement departement;
    @Enumerated(EnumType.STRING)
    private TypeCapteur type;
    private Double valeurActuelle; // For TEMPERATURE and HUMIDITE
    @Enumerated(EnumType.STRING)
    private EtatElectricite etatElectricite; // For ELECTRICITE
    private LocalDateTime derniereMiseAJour;
    private String uniteMesure;

    @OneToMany(mappedBy = "capteur", fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnore // Ajoutez cette ligne si vous ne voulez pas sérialiser les alertes

    private List<Alerte> alertes;




    public enum TypeCapteur {
        TEMPERATURE, ELECTRICITE, HUMIDITE
    }

    public enum Departement {
        MANOUBA, MANAR
    }

    public enum EtatElectricite {
        EN_MARCHE, ARRET
    }

    public Double getValeurActuelle() {
        return valeurActuelle;
    }

    public LocalDateTime getDerniereMiseAJour() {
        return derniereMiseAJour;
    }

    public void setIpAdresse(String ipAdresse) {
        this.ipAdresse = ipAdresse;
    }

    public EtatElectricite getEtatElectricite() {
        return etatElectricite;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public void setIdCapt(Long idCapt) {
        this.idCapt = idCapt;
    }

    public Long getIdCapt() {
        return idCapt;
    }

    public TypeCapteur getType() {
        return type;
    }

    public String getIpAdresse() {
        return ipAdresse;
    }

    public void setType(TypeCapteur type) {
        this.type = type;
    }

    public String getUniteMesure() {
        return uniteMesure;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public void setUniteMesure(String uniteMesure) {
        this.uniteMesure = uniteMesure;
    }

    public String getNomComplet() {
        return String.format("%s - %s (%s)", emplacement, type, departement);
    }

    public void mettreAJourValeur(Double nouvelleValeur) {
        if (type == TypeCapteur.ELECTRICITE) {
            this.etatElectricite = nouvelleValeur == 1.0 ? EtatElectricite.EN_MARCHE : EtatElectricite.ARRET;
        } else {
            this.valeurActuelle = nouvelleValeur;
        }
        this.derniereMiseAJour = LocalDateTime.now();
    }
}