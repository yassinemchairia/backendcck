package com.example.cckback.controller;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AlerteRepository;
import com.example.cckback.Repository.CapteurRepository;
import com.example.cckback.dto.AlerteDTO;
import com.example.cckback.dto.TechnicienDTO;
import com.example.cckback.service.AlerteService;
import com.example.cckback.service.TechnicienService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/alertes")

public class AlerteController {
    @Autowired
    private AlerteRepository alerteRepository;
    private final AlerteService alerteService;

    private final CapteurRepository capteurRepository;

    private final TechnicienService technicienService;

    @Autowired
    public AlerteController(AlerteService alerteService, CapteurRepository capteurRepository,TechnicienService technicienService) {
        this.alerteService = alerteService;
        this.capteurRepository = capteurRepository;
        this.technicienService = technicienService;
    }
    @GetMapping("/by-type")
    public Map<Alerte.TypePanne, Long> getAlertesByType() {
        return alerteService.countAlertesByType();
    }

    @GetMapping("/by-gravite")
    public Map<Alerte.NiveauGravite, Long> getAlertesByGravite() {
        return alerteService.countAlertesByGravite();
    }
    @GetMapping("/historiques")
    public ResponseEntity<List<AlerteHistoriqueDTO>> getHistoricalAlertes() {
        List<Alerte> alertes = alerteRepository.findByEstResolu(true);
        List<AlerteHistoriqueDTO> dtos = alertes.stream().map(alerte -> {
            AlerteHistoriqueDTO dto = new AlerteHistoriqueDTO();
            dto.setIdAlerte(alerte.getIdAlerte());
            dto.setTypePanne(alerte.getTypePanne().toString());
            dto.setNiveauGravite(alerte.getNiveauGravite().toString());
            dto.setValeurDeclenchement(alerte.getValeurDeclenchement());
            dto.setTypeCapteur(alerte.getCapteur().getType().toString());
            dto.setEmplacement(alerte.getCapteur().getEmplacement());
            dto.setDescription(alerte.getDescription());
            // Récupérer le rapport et les détails depuis la première intervention
            if (!alerte.getInterventions().isEmpty()) {
                Intervention intervention = alerte.getInterventions().get(0);
                RapportIntervention rapport = intervention.getRapport();
                if (rapport != null) {
                    dto.setDetails(rapport.getDetails());
                    dto.setSatisfaction(rapport.getSatisfaction());
                }
                if (!intervention.getHistorique().isEmpty()) {
                    dto.setRapport(intervention.getHistorique().get(0).getRapport());
                }
            }
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/temps-moyen-resolution")
    public double getAverageResolutionTime() {
        return alerteService.averageResolutionTime();
    }
    @GetMapping("/taux-resolution")
    public double getResolutionRate() {
        return alerteService.resolutionRate();
    }

    @GetMapping("/liste")
    public ResponseEntity<?> getAllAlertes() {
        try {
            List<Alerte> alertes = alerteService.getAllAlertesAvecCapteurs();
            List<AlerteDTO> alertesDTO = alertes.stream()
                    .map(AlerteDTO::new)
                    .toList();

            return ResponseEntity.ok(alertesDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des alertes : " + e.getMessage());
        }
    }
    @GetMapping("/type")
    public ResponseEntity<?> getAlertesParType(@RequestParam Alerte.TypePanne typePanne) {
        List<AlerteDTO> alertes = alerteService.getAlertesParType(typePanne)
                .stream().map(AlerteDTO::new).toList();
        return ResponseEntity.ok(alertes);
    }

    @GetMapping("/gravite")
    public ResponseEntity<?> getAlertesParGravite(@RequestParam Alerte.NiveauGravite niveauGravite) {
        List<AlerteDTO> alertes = alerteService.getAlertesParGravite(niveauGravite)
                .stream().map(AlerteDTO::new).toList();
        return ResponseEntity.ok(alertes);
    }

    @GetMapping("/resolution")
    public ResponseEntity<?> getAlertesParResolution(@RequestParam boolean estResolu) {
        List<AlerteDTO> alertes = alerteService.getAlertesParResolution(estResolu)
                .stream().map(AlerteDTO::new).toList();
        return ResponseEntity.ok(alertes);
    }
    @GetMapping("/search")
    public ResponseEntity<?> searchAlertes(
            @RequestParam(required = false) Alerte.TypePanne typePanne,
            @RequestParam(required = false) Alerte.NiveauGravite niveauGravite,
            @RequestParam(required = false) Boolean estResolu) {

        List<AlerteDTO> alertes = alerteService.searchAlertes(typePanne, niveauGravite, estResolu)
                .stream().map(AlerteDTO::new).toList();
        return ResponseEntity.ok(alertes);
    }

    @GetMapping("/techniciens")
    public List<TechnicienDTO> getTechniciens() {
        return technicienService.getAllTechniciens();
    }



}
