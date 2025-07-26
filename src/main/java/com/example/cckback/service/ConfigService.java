package com.example.cckback.service;

import com.example.cckback.Entity.Alerte;
import com.example.cckback.Entity.Capteur;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ConfigurationProperties(prefix = "capteurs.seuils")
public class ConfigService {

    private final Map<Capteur.TypeCapteur, SeuilConfig> configurations = new ConcurrentHashMap<>();

    public SeuilConfig getSeuils(Capteur.TypeCapteur type) {
        return configurations.computeIfAbsent(type, SeuilConfig::getDefault);
    }

    public void setTemperature(SeuilConfig temperature) {
        configurations.put(Capteur.TypeCapteur.TEMPERATURE, temperature);
    }

    public void setHumidite(SeuilConfig humidite) {
        configurations.put(Capteur.TypeCapteur.HUMIDITE, humidite);
    }

    public void setElectricite(SeuilConfig electricite) {
        configurations.put(Capteur.TypeCapteur.ELECTRICITE, electricite);
    }

    public static class SeuilConfig {
        private Double basCritiqueMin;
        private Double basCritiqueMax;
        private Double basMin;
        private Double basMax;
        private Double normaleMin;
        private Double normaleMax;
        private Double critiqueMin;
        private Double critiqueMax;
        private Double highCritiqueMin;
        private Double highCritiqueMax;

        public Alerte.NiveauGravite determinerNiveau(Double valeur) {
            if (valeur == null) return Alerte.NiveauGravite.NORMALE;

            if (valeur >= basCritiqueMin && valeur < basCritiqueMax) return Alerte.NiveauGravite.BAS_CRITIQUE;
            if (valeur >= basMin && valeur < basMax) return Alerte.NiveauGravite.BAS;
            if (valeur >= normaleMin && valeur < normaleMax) return Alerte.NiveauGravite.NORMALE;
            if (valeur >= critiqueMin && valeur < critiqueMax) return Alerte.NiveauGravite.CRITIQUE;
            if (valeur >= highCritiqueMin && valeur <= highCritiqueMax) return Alerte.NiveauGravite.HIGH_CRITICAL;
            return Alerte.NiveauGravite.NORMALE;
        }

        public static SeuilConfig getDefault(Capteur.TypeCapteur type) {
            SeuilConfig config = new SeuilConfig();
            switch (type) {
                case TEMPERATURE:
                    config.basCritiqueMin = -55.0; config.basCritiqueMax = 10.0;
                    config.basMin = 10.0; config.basMax = 17.0;
                    config.normaleMin = 17.0; config.normaleMax = 28.0;
                    config.critiqueMin = 28.0; config.critiqueMax = 40.0;
                    config.highCritiqueMin = 40.0; config.highCritiqueMax = 75.0;
                    break;
                case HUMIDITE:
                    config.basCritiqueMin = 0.0; config.basCritiqueMax = 30.0;
                    config.basMin = 30.0; config.basMax = 40.0;
                    config.normaleMin = 40.0; config.normaleMax = 80.0;
                    config.critiqueMin = 80.0; config.critiqueMax = 90.0;
                    config.highCritiqueMin = 90.0; config.highCritiqueMax = 100.0;
                    break;
                case ELECTRICITE:
                    config.basCritiqueMin = 0.0; config.basCritiqueMax = 0.0;
                    config.normaleMin = 1.0; config.normaleMax = 1.0;
                    config.basMin = 0.0; config.basMax = 0.0;
                    config.critiqueMin = 0.0; config.critiqueMax = 0.0;
                    config.highCritiqueMin = 0.0; config.highCritiqueMax = 0.0;
                    break;
            }
            return config;
        }

        public Double getBasCritiqueMin() { return basCritiqueMin; }
        public void setBasCritiqueMin(Double basCritiqueMin) { this.basCritiqueMin = basCritiqueMin; }
        public Double getBasCritiqueMax() { return basCritiqueMax; }
        public void setBasCritiqueMax(Double basCritiqueMax) { this.basCritiqueMax = basCritiqueMax; }
        public Double getBasMin() { return basMin; }
        public void setBasMin(Double basMin) { this.basMin = basMin; }
        public Double getBasMax() { return basMax; }
        public void setBasMax(Double basMax) { this.basMax = basMax; }
        public Double getNormaleMin() { return normaleMin; }
        public void setNormaleMin(Double normaleMin) { this.normaleMin = normaleMin; }
        public Double getNormaleMax() { return normaleMax; }
        public void setNormaleMax(Double normaleMax) { this.normaleMax = normaleMax; }
        public Double getCritiqueMin() { return critiqueMin; }
        public void setCritiqueMin(Double critiqueMin) { this.critiqueMin = critiqueMin; }
        public Double getCritiqueMax() { return critiqueMax; }
        public void setCritiqueMax(Double critiqueMax) { this.critiqueMax = critiqueMax; }
        public Double getHighCritiqueMin() { return highCritiqueMin; }
        public void setHighCritiqueMin(Double highCritiqueMin) { this.highCritiqueMin = highCritiqueMin; }
        public Double getHighCritiqueMax() { return highCritiqueMax; }
        public void setHighCritiqueMax(Double highCritiqueMax) { this.highCritiqueMax = highCritiqueMax; }
    }
}