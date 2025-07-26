package com.example.cckback.service;

import com.example.cckback.Entity.Alerte;
import com.example.cckback.Entity.Intervention;
import com.example.cckback.Entity.RapportIntervention;
import com.example.cckback.Entity.TypeIntervention;
import com.example.cckback.Entity.PrioriteIntervention;
import com.example.cckback.Entity.Statut;
import com.example.cckback.Repository.AlerteRepository;
import com.example.cckback.dto.AlertessDTO;
import com.example.cckback.dto.Intervention1DTO;
import com.example.cckback.dto.InterventionDTO;
import com.example.cckback.dto.InterventionssDTO;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class AlertePredictionService {

    private static final Logger LOGGER = Logger.getLogger(AlertePredictionService.class.getName());
    private static final String FLASK_API_URL = "http://localhost:5000/predict_solution";

    @Autowired
    private RestTemplate restTemplate;
@Autowired
 private AlerteRepository alerteRepository;
    @Autowired
    private InterventionService interventionService;

    @Autowired
    private AlerteService alerteService;

    public Map<String, Object> predictSolution(AlertessDTO alerteDTO) {
        try {
            // Prepare the request payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("typePanne", alerteDTO.getTypePanne());
            payload.put("niveauGravite", alerteDTO.getNiveauGravite());
            payload.put("valeurDeclenchement", alerteDTO.getValeurDeclenchement());
            payload.put("typeCapteur", alerteDTO.getTypeCapteur());
            payload.put("emplacement", alerteDTO.getEmplacement());
            payload.put("description", alerteDTO.getDescription());

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HTTP entity
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            // Send POST request to Flask API
            ResponseEntity<Map> response = restTemplate.postForEntity(FLASK_API_URL, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Prediction received successfully from Flask API");
                return response.getBody();
            } else {
                LOGGER.severe("Error from Flask API: HTTP " + response.getStatusCodeValue());
                throw new RuntimeException("Failed to get prediction from Flask API: HTTP " + response.getStatusCodeValue());
            }
        } catch (Exception e) {
            LOGGER.severe("Error during prediction request: " + e.getMessage());
            throw new RuntimeException("Error during prediction request", e);
        }
    }

    public Intervention createInterventionFromPrediction(Alerte alerte, Map<String, Object> prediction) {
        try {
            String solution = (String) prediction.get("solution");

            // Create a new Intervention
            Intervention intervention = new Intervention();
            intervention.setAlerte(alerte);
            intervention.setDateDebut(LocalDateTime.now());
            intervention.setDateFin(LocalDateTime.now()); // Set dateFin to current date/time
            intervention.setStatut(Statut.TERMINEE); // Set to TERMINEE since resolved
            intervention.setTypeIntervention(TypeIntervention.CORRECTIVE);
            intervention.setPriorite(mapNiveauGraviteToPriorite(alerte.getNiveauGravite()));
            intervention.setResolvedByAI(true); // Set AI flag

            // Create a RapportIntervention with the predicted solution and default satisfaction
            RapportIntervention rapport = new RapportIntervention();
            rapport.setDetails(solution);
            rapport.setCoutIntervention(0.0); // Placeholder, can be updated later
            rapport.setSatisfaction(5); // Set default satisfaction to 5
            rapport.setIntervention(intervention);

            intervention.setRapport(rapport);

            // Mark the alert as resolved
            alerte.setEstResolu(true);
            alerte.setDateResolution(LocalDateTime.now());
            alerteService.saveAlerte(alerte); // Save the updated alert

            // Save the intervention
            interventionService.saveIntervention(intervention);

            LOGGER.info("Intervention created for alert ID " + alerte.getIdAlerte() + " with solution: " + solution);
            return intervention;
        } catch (Exception e) {
            LOGGER.severe("Error creating intervention from prediction: " + e.getMessage());
            throw new RuntimeException("Error creating intervention from prediction", e);
        }
    }

    private PrioriteIntervention mapNiveauGraviteToPriorite(Alerte.NiveauGravite niveauGravite) {
        switch (niveauGravite) {
            case HIGH_CRITICAL:
            case CRITIQUE:
                return PrioriteIntervention.ELEVEE;
            case NORMALE:
                return PrioriteIntervention.MOYENNE;
            case BAS:
            case BAS_CRITIQUE:
                return PrioriteIntervention.BASSE;
            default:
                return PrioriteIntervention.MOYENNE;
        }
    }
    public List<Alerte> getResolvedAIAlerts() {
        return alerteRepository.findByEstResoluTrueAndInterventionsResolvedByAITrue();
    }
    public List<Alerte> getResolvedAIAlertsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return alerteRepository.findByEstResoluTrueAndInterventionsResolvedByAITrueAndDateAlerteBetween(startDate, endDate);
    }
    public Double getAverageSatisfactionForAIAlerts() {
        List<Alerte> alerts = alerteRepository.findByEstResoluTrueAndInterventionsResolvedByAITrue();
        return alerts.stream()
                .flatMap(a -> a.getInterventions().stream())
                .filter(i -> i.isResolvedByAI() && i.getRapport() != null)
                .mapToInt(i -> i.getRapport().getSatisfaction())
                .average()
                .orElse(0.0);
    }
    public Optional<Intervention1DTO> getInterventionDetailsForAlert(Long alerteId) {
        Alerte alerte = alerteRepository.findById(alerteId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        Optional<Intervention> intervention = alerte.getInterventions().stream()
                .filter(i -> i.isResolvedByAI())
                .findFirst();
        return intervention.map(i -> new Intervention1DTO( // Changed from InterventionDTO to Intervention1DTO
                i.getIdInterv(),
                i.getAlerte().getIdAlerte(),
                i.getDateDebut(),
                i.getDateFin(),
                i.getStatut(),
                i.getTypeIntervention(),
                i.getPriorite(),
                i.getRapport() != null ? i.getRapport().getDetails() : null,
                i.getRapport() != null ? i.getRapport().getSatisfaction() : null
        ));
    }
    public String exportResolvedAIAlertsToCSV() {
        List<Alerte> alerts = alerteRepository.findByEstResoluTrueAndInterventionsResolvedByAITrue();
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer);

        // Write CSV header
        String[] header = {
                "ID Alerte", "Type Panne", "Niveau Gravite", "Date Alerte",
                "Description", "Valeur Declenchement", "Type Capteur", "Emplacement",
                "Solution", "Satisfaction"
        };
        csvWriter.writeNext(header);

        // Write alert data
        for (Alerte alerte : alerts) {
            Optional<Intervention> intervention = alerte.getInterventions().stream()
                    .filter(i -> i.isResolvedByAI())
                    .findFirst();
            String solution = intervention.map(i -> i.getRapport() != null ? i.getRapport().getDetails() : "").orElse("");
            String satisfaction = intervention.map(i -> i.getRapport() != null ? String.valueOf(i.getRapport().getSatisfaction()) : "").orElse("");
            String[] row = {
                    String.valueOf(alerte.getIdAlerte()),
                    alerte.getTypePanne().toString(),
                    alerte.getNiveauGravite().toString(),
                    alerte.getDateAlerte().toString(),
                    alerte.getDescription(),
                    String.valueOf(alerte.getValeurDeclenclement()),
                    alerte.getCapteur() != null ? alerte.getCapteur().getType().toString() : "",
                    alerte.getCapteur() != null ? alerte.getCapteur().getEmplacement() : "",
                    solution,
                    satisfaction
            };
            csvWriter.writeNext(row);
        }

        try {
            csvWriter.close();
        } catch (Exception e) {
            LOGGER.severe("Error closing CSV writer: " + e.getMessage());
            throw new RuntimeException("Error generating CSV", e);
        }

        return writer.toString();
    }
}