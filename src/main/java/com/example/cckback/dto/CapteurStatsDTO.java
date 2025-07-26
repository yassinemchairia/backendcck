package com.example.cckback.dto;

import com.example.cckback.Entity.Capteur;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CapteurStatsDTO {
    private Long capteurId;
    private String emplacement;
    private String ipAdresse;
    private Capteur.TypeCapteur type;
    private Long nombreAlertes;

    public String getEmplacement() {
        return emplacement;
    }
    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }
    public String getIpAdresse() {
        return ipAdresse;
    }
    public void setIpAdresse(String ipAdresse) {
        this.ipAdresse = ipAdresse;
    }
    public Capteur.TypeCapteur getType() {
        return type;
    }
    public void setType(Capteur.TypeCapteur type) {
        this.type = type;
    }
    public Long getNombreAlertes() {
        return nombreAlertes;
    }
    public void setNombreAlertes(Long nombreAlertes) {
        this.nombreAlertes = nombreAlertes;
    }
    public Long getCapteurId() {
        return capteurId;
    }
    public void setCapteurId(Long capteurId) {
        this.capteurId = capteurId;
    }

}
