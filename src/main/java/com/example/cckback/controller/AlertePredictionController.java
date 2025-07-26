package com.example.cckback.controller;

import com.example.cckback.Entity.Alerte;
import com.example.cckback.Entity.Intervention;
import com.example.cckback.dto.AlertessDTO;
import com.example.cckback.dto.Intervention1DTO;
import com.example.cckback.dto.InterventionssDTO;
import com.example.cckback.service.AlertePredictionService;
import com.example.cckback.service.AlerteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/alertes")
public class AlertePredictionController {

    @Autowired
    private AlertePredictionService alertePredictionService;

    @Autowired
    private AlerteService alerteService;

    @PostMapping("/predict")
    public ResponseEntity<Map<String, Object>> predictSolution(@RequestBody AlertessDTO alerteDTO) {
        Map<String, Object> prediction = alertePredictionService.predictSolution(alerteDTO);
        return ResponseEntity.ok(prediction);
    }

    @PostMapping("/predict-and-create-intervention/{alerteId}")
    public ResponseEntity<Intervention> predictAndCreateIntervention(@PathVariable Long alerteId) {
        Alerte alerte = alerteService.findById(alerteId);
        if (alerte == null) {
            return ResponseEntity.notFound().build();
        }

        AlertessDTO alerteDTO = new AlertessDTO(
                alerte.getIdAlerte(),
                alerte.getTypePanne().toString(),
                alerte.getNiveauGravite().toString(),
                alerte.getValeurDeclenclement(),
                alerte.getCapteur().getType().toString(),
                alerte.getCapteur().getEmplacement(),
                alerte.getDescription(),
                "", // Solution will be predicted
                0 // Satisfaction will be set later
        );

        Map<String, Object> prediction = alertePredictionService.predictSolution(alerteDTO);
        Intervention intervention = alertePredictionService.createInterventionFromPrediction(alerte, prediction);
        return ResponseEntity.ok(intervention);
    }
    @GetMapping("/resolved-ai")
    public ResponseEntity<List<Alerte>> getResolvedAIAlerts() {
        try {
            List<Alerte> alerts = alertePredictionService.getResolvedAIAlerts();
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/resolved-ai/by-date-range")
    public ResponseEntity<List<Alerte>> getResolvedAIAlertsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        System.out.println("Received startDate: " + startDate + ", endDate: " + endDate);
        List<Alerte> alerts = alertePredictionService.getResolvedAIAlertsByDateRange(startDate, endDate);
        return ResponseEntity.ok(alerts);
    }
    @GetMapping("/resolved-ai/average-satisfaction")
    public ResponseEntity<Double> getAverageSatisfactionForAIAlerts() {
        try {
            Double avgSatisfaction = alertePredictionService.getAverageSatisfactionForAIAlerts();
            return ResponseEntity.ok(avgSatisfaction);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/resolved-ai/{alerteId}/intervention")
    public ResponseEntity<Intervention1DTO> getInterventionDetailsForAlert(@PathVariable Long alerteId) {
        try {
            Optional<Intervention1DTO> intervention = alertePredictionService.getInterventionDetailsForAlert(alerteId);
            return intervention.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/resolved-ai/export-csv")
    public ResponseEntity<byte[]> exportResolvedAIAlertsToCSV() {
        try {
            String csvContent = alertePredictionService.exportResolvedAIAlertsToCSV();
            byte[] csvBytes = csvContent.getBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "resolved-ai-alerts.csv");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvBytes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}