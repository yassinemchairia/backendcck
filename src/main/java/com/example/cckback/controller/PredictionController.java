package com.example.cckback.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/predict")
public class PredictionController {

    @Value("${flask.api.url}")
    private String flaskApiUrl;

    private final RestTemplate restTemplate;

    public PredictionController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<?> predictTechnician(@RequestBody InterventionPredictionRequest request) {
        try {
            // Ajoutez un log pour vérifier l'URL
            System.out.println("URL Flask: " + flaskApiUrl + "/predict");

            String url = flaskApiUrl + "/predict";
            if (!url.startsWith("http")) {
                url = "http://" + url; // Garantit que l'URL est absolue
            }

            PredictionResponse response = restTemplate.postForObject(
                    url,
                    request,
                    PredictionResponse.class
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Log complet de l'erreur
            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "status", "error",
                            "message", "Erreur lors de la prédiction",
                            "details", e.getMessage()
                    )
            );
        }
    }

    // Classes internes pour la requête et la réponse
    public static class InterventionPredictionRequest {
        private String dateDebut;
        private String specialite;
        private String typeIntervention;
        private String priorite;
        private double dureeEstimee;

        public String getTypeIntervention() {
            return typeIntervention;
        }
        public void setTypeIntervention(String typeIntervention) {
            this.typeIntervention = typeIntervention;
        }
        public String getDateDebut() {
            return dateDebut;
        }
        public void setDateDebut(String dateDebut) {
            this.dateDebut = dateDebut;
        }
        public String getSpecialite() {
            return specialite;
        }
        public void setSpecialite(String specialite) {
            this.specialite = specialite;
        }
        public String getPriorite() {
            return priorite;
        }
        public void setPriorite(String priorite) {
            this.priorite = priorite;
        }
        public double getDureeEstimee() {
            return dureeEstimee;
        }
        public void setDureeEstimee(double dureeEstimee) {
            this.dureeEstimee = dureeEstimee;
        }

// Getters et Setters
    }

    public static class PredictionResponse {
        private String status;
        private List<Integer> techniciens;
        private List<Double> probabilites;
        private String specialiteDemandee;
public String getStatus() {
    return status;
}
public void setStatus(String status) {
    this.status = status;
}
public List<Integer> getTechniciens() {
    return techniciens;
}
public void setTechniciens(List<Integer> techniciens) {
    this.techniciens = techniciens;
}
public List<Double> getProbabilites() {
    return probabilites;
}
public void setProbabilites(List<Double> probabilites) {
    this.probabilites = probabilites;
}
public String getSpecialiteDemandee() {
    return specialiteDemandee;
}
public void setSpecialiteDemandee(String specialiteDemandee) {
    this.specialiteDemandee = specialiteDemandee;

}

        // Getters et Setters
    }
}