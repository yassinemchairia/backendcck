package com.example.cckback.controller;

import com.example.cckback.Entity.RapportIntervention;
import com.example.cckback.service.RapportInterventionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rapport")
public class RapportInterventionController {

    @Autowired
    private RapportInterventionService rapportInterventionService;

    @PostMapping("/ajouterRapport/{interventionId}")
    public RapportIntervention ajouterRapport(
            @PathVariable Long interventionId,
            @RequestBody RapportIntervention rapport) {
        return rapportInterventionService.ajouterRapport(interventionId, rapport);
    }
    @GetMapping("/mesrapport")
    public ResponseEntity<List<RapportIntervention>> getRapportsByUserId(@RequestParam Long idUser) {
        List<RapportIntervention> rapports = rapportInterventionService.getRapportsByUserId(idUser);
        return ResponseEntity.ok(rapports);
    }
}