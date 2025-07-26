package com.example.cckback.dto;

import com.example.cckback.Entity.Alerte;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Getter
@Setter
public class AlerteDTO {
    private Long idAlerte;
    private String typePanne;
    private String niveauGravite;
    private LocalDateTime dateAlerte;
    private boolean estResolu;

    private Long idCapteur;
    private String ipAdresse;
    private String emplacement;
    private String typeCapteur;

    public AlerteDTO(Alerte alerte) {
        this.idAlerte = alerte.getIdAlerte();
        this.typePanne = alerte.getTypePanne().toString();
        this.niveauGravite = alerte.getNiveauGravite().toString();
        this.dateAlerte = alerte.getDateAlerte();
        this.estResolu = alerte.getEstResolu();

        if (alerte.getCapteur() != null) {
            this.idCapteur = alerte.getCapteur().getIdCapt();
            this.ipAdresse = alerte.getCapteur().getIpAdresse();
            this.emplacement = alerte.getCapteur().getEmplacement();
            this.typeCapteur = alerte.getCapteur().getType().toString();
        }
    }

    public String getIpAdresse() {
        return ipAdresse;
    }

    public boolean isEstResolu() {
        return estResolu;
    }

    public Long getIdAlerte() {
        return idAlerte;
    }

    public LocalDateTime getDateAlerte() {
        return dateAlerte;
    }
    public String getTypePanne() {
        return typePanne;
    }
    public String getNiveauGravite() {
        return niveauGravite;
    }

    public String getTypeCapteur() {
        return typeCapteur;
    }

    public Long getIdCapteur() {
        return idCapteur;
    }

    public String getEmplacement() {
        return emplacement;
    }
    // Getters (ou utilise Lombok @Getter)
}
