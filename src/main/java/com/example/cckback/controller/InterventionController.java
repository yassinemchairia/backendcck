package com.example.cckback.controller;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AlerteRepository;
import com.example.cckback.Repository.InterventionRepository;
import com.example.cckback.Repository.TechnicienRepository;
import com.example.cckback.dto.*;
import com.example.cckback.service.InterventionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/interventions")
public class InterventionController {

    @Autowired
    private InterventionRepository interventionRepository;

    @Autowired
    private AlerteRepository alerteRepository;

    @Autowired
    private TechnicienRepository technicienRepository;
    @Autowired
    private InterventionService interventionService;

    @GetMapping("afficher-intervention")
    public List<InterventionDTO> getAllInterventions() {
        return interventionService.getAllInterventions();
    }
    @GetMapping("/technicien/{id}")
    public List<InterventionDTO> getInterventionsByTechnicien(@PathVariable("id") Long idTechnicien) {
        return interventionService.getInterventionsByTechnicienId(idTechnicien);
    }
    @PostMapping("/ajouter")
    public ResponseEntity<?> ajouterIntervention(@RequestBody InterventionRequestDTO interventionRequest) {
        // Validate required fields
        if (interventionRequest.getIdAlerte() == null ||
                interventionRequest.getTechnicienIds() == null ||
                interventionRequest.getTechnicienIds().isEmpty() ||
                interventionRequest.getDateDebut() == null) {
            return ResponseEntity.badRequest().body("L'alerte, les techniciens et la date de début sont obligatoires.");
        }

        // Validate dateDebut format
        LocalDateTime dateDebut;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            dateDebut = LocalDateTime.parse(interventionRequest.getDateDebut(), formatter);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Format de date invalide. Utilisez yyyy-MM-ddTHH:mm (ex. 2025-07-25T09:00).");
        }

        // Validate alerte
        Optional<Alerte> alerteOpt = alerteRepository.findById(interventionRequest.getIdAlerte());
        if (alerteOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("L'alerte spécifiée n'existe pas.");
        }

        // Validate techniciens
        List<Technicien> techniciens = technicienRepository.findAllById(interventionRequest.getTechnicienIds());
        if (techniciens.isEmpty()) {
            return ResponseEntity.badRequest().body("Aucun technicien valide sélectionné.");
        }

        try {
            Intervention intervention = interventionService.ajouterIntervention(
                    dateDebut,
                    interventionRequest.getPriorite(),
                    interventionRequest.getTypeIntervention(),
                    interventionRequest.getTechnicienIds(),
                    interventionRequest.getIdAlerte()
            );
            return ResponseEntity.ok(intervention);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }
    @PostMapping("/prediction/technicien")
    public ResponseEntity<List<Long>> predictTechnicien(@RequestBody PredictionRequest request) {
        try {
            String flaskUrl = "http://localhost:5000/predict";
            RestTemplate restTemplate = new RestTemplate();

            // Le type attendu est maintenant une liste de Long
            ResponseEntity<List> response = restTemplate.postForEntity(flaskUrl, request, List.class);

            // Conversion sécurisée
            List<Long> ids = new ArrayList<>();
            for (Object id : response.getBody()) {
                ids.add(Long.valueOf(id.toString()));
            }

            return ResponseEntity.ok(ids);
        } catch (Exception e) {
            e.printStackTrace(); // Pour mieux déboguer
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/vvbvtechnicien/{id}")
    public List<InterventionCalendarDTO> getInterventionsForCalendar(
            @PathVariable("id") Long technicienId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        if (start == null && end == null) {
            // Si aucune date n'est fournie, retourner les interventions du jour
            LocalDate today = LocalDate.now();
            return interventionService.getInterventionsForCalendarByDate(technicienId, today);
        } else if (start != null && end == null) {
            // Si seule la date de début est fournie
            return interventionService.getInterventionsForCalendarByDate(technicienId, start);
        } else {
            // Si les deux dates sont fournies
            return interventionService.getInterventionsForCalendar(technicienId, start, end);
        }
    }
    @GetMapping("/{id}/techniciens")
    public ResponseEntity<List<TechnicienDTO>> getTechniciensAffectes(@PathVariable Long id) {
        List<TechnicienDTO> techniciens = interventionService.getTechniciensAffectes(id);

        if (techniciens.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si aucun technicien
        }

        return ResponseEntity.ok(techniciens); // 200 OK avec la liste des techniciens
    }
    @GetMapping("/by-type")
    public Map<TypeIntervention, Long> getInterventionsByType(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate != null && endDate != null) {
            return interventionService.countInterventionsByTypeBetweenDates(startDate, endDate);
        }
        return interventionService.countInterventionsByType();
    }

    @GetMapping("/by-status")
    public Map<Statut, Long> getInterventionsByStatus() {
        return interventionService.countInterventionsByStatus();
    }

    @GetMapping("/average-duration")
    public double getAverageInterventionDuration() {
        return interventionService.calculateAverageInterventionDuration();
    }

    @GetMapping("/by-priority")
    public Map<PrioriteIntervention, Long> getInterventionsByPriority() {
        return interventionService.countInterventionsByPriority();
    }

}