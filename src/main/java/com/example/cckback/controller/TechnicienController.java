package com.example.cckback.controller;



import com.example.cckback.Entity.Specialite;
import com.example.cckback.Entity.Technicien;
import com.example.cckback.dto.PredictionRequest;
import com.example.cckback.dto.TechnicienStatistiqueDTO;
import com.example.cckback.service.TechnicienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TechnicienController {

    @Autowired
    private TechnicienService technicienService;

    @GetMapping("/interventions-count")
    public Map<String, Long> getInterventionsByTechnicien() {
        return technicienService.countInterventionsByTechnicien();
    }

    @GetMapping("/satisfaction-moyenne")
    public Map<String, Double> getSatisfactionMoyenne() {
        return technicienService.averageSatisfactionByTechnicien();
    }

    @GetMapping("/disponibilite")
    public Map<String, Double> getDisponibilite() {
        return technicienService.calculateAvailabilityRate();
    }

    @GetMapping("/specialites-sollicitees")
    public Map<Specialite, Long> getSpecialitesSollicitees() {
        return technicienService.countInterventionsBySpecialite();
    }

    @GetMapping("/full-stats")
    public List<TechnicienStatistiqueDTO> getFullTechniciensStats() {
        return technicienService.getTechniciensStats();
    }
    @GetMapping("/specialite/{specialite}")
    public List<Technicien> getTechniciensParSpecialite(@PathVariable Specialite specialite) {
        return technicienService.getTechniciensParSpecialite(specialite);
    }
    @PostMapping("/predict")
    public ResponseEntity<Integer> predictTechnicien(@RequestBody PredictionRequest request) {
        int technicienId = technicienService.predictTechnicien(request);
        if (technicienId != -1) {
            return ResponseEntity.ok(technicienId);
        } else {
            return ResponseEntity.status(500).body(null);
        }
    }


}

