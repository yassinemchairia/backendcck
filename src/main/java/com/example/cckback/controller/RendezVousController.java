package com.example.cckback.controller;

import com.example.cckback.Entity.RendezVous;
import com.example.cckback.dto.AutoPlanificationRequest;
import com.example.cckback.dto.RendezVousRequest;
import com.example.cckback.dto.RendezVousStatsDTO;
import com.example.cckback.service.RendezVousService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rendezvous")
public class RendezVousController {

    private final RendezVousService rendezVousService;

    public RendezVousController(RendezVousService rendezVousService) {
        this.rendezVousService = rendezVousService;
    }
    /**
     * Get all appointments for a given user ID
     * @param idUser The ID of the user (Technicien or Administrateur)
     * @return List of RendezVous
     */
    @GetMapping("/user")
    public ResponseEntity<List<RendezVous>> getRendezVousByUserId(@RequestParam Long idUser) {
        List<RendezVous> rendezVousList = rendezVousService.getRendezVousByUserId(idUser);
        return ResponseEntity.ok(rendezVousList);
    }
    @PostMapping("/ajouter")
    public ResponseEntity<?> ajouterRendezVous(@RequestBody RendezVousRequest request) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(request.getDate());
            LocalDateTime dateRendezVous = zonedDateTime.toLocalDateTime();
            RendezVous rendezVous = rendezVousService.ajouterRendezVous(
                    request.getAdminId(),
                    request.getDescription(),
                    dateRendezVous,
                    request.getTechnicienIds()
            );
            return ResponseEntity.ok(rendezVous);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur : " + e.getMessage());
        }
    }

    @PostMapping("/suggerer")
    @ResponseBody
    public ResponseEntity<?> suggererRendezVous(@RequestBody AutoPlanificationRequest request) {
        try {
            Map<String, Object> response = rendezVousService.suggererPlanification(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur : " + e.getMessage());
        }
    }

    @GetMapping("/by-month")
    public Map<String, Long> getRendezVousByMonth(
            @RequestParam(defaultValue = "6") int monthsBack) {
        return rendezVousService.countRendezVousByMonth(monthsBack);
    }

    @GetMapping("/by-week")
    public Map<String, Long> getRendezVousByWeek(
            @RequestParam(defaultValue = "8") int weeksBack) {
        return rendezVousService.countRendezVousByWeek(weeksBack);
    }

    @GetMapping("/participation")
    public Map<String, Double> getParticipationRates() {
        return rendezVousService.calculateParticipationRate();
    }

    @GetMapping("/full-stats")
    public List<RendezVousStatsDTO> getFullRendezVousStats() {
        return rendezVousService.getRendezVousStats();
    }
}