package com.example.cckback.controller;

import com.example.cckback.Entity.HistoriqueIntervention;
import com.example.cckback.dto.HistoriqueInterventionDTO;
import com.example.cckback.dto.HistoriqueInterventionListDTO;
import com.example.cckback.service.HistoriqueInterventionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historique")
public class HistoriqueInterventionController {

    @Autowired
    private HistoriqueInterventionService historiqueInterventionService;

    @PostMapping("/ajouter-historique")
    public ResponseEntity<?> ajouterHistorique(@RequestBody HistoriqueInterventionDTO dto) {
        try {
            HistoriqueIntervention historique = historiqueInterventionService.ajouterHistorique(
                    dto.getInterventionId(),
                    dto.getDescription(),
                    dto.getRapport(),
                    dto.getStatut()
            );
            return ResponseEntity.ok(historique);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Erreur de logique métier : " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Erreur : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur interne du serveur.");
        }
    }

    @PostMapping("/mise-a-jour")
    public ResponseEntity<?> ajouterMiseAJour(
            @RequestParam Long interventionId,
            @RequestParam String description,
            @RequestParam String rapport) {
        try {
            HistoriqueIntervention historique = historiqueInterventionService.ajouterMiseAJour(
                    interventionId, description, rapport);
            return ResponseEntity.ok(historique);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/intervention/{idInterv}")
    public ResponseEntity<?> getHistoriquesParIntervention(@PathVariable Long idInterv) {
        try {
            List<HistoriqueInterventionListDTO> historiques = historiqueInterventionService.getHistoriquesParIntervention(idInterv);
            return ResponseEntity.ok(historiques);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la récupération des historiques.");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchHistoriques(@RequestParam String term) {
        try {
            List<HistoriqueInterventionListDTO> historiques = historiqueInterventionService.searchHistoriquesByDescriptionOrRapport(term);
            return ResponseEntity.ok(historiques);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la recherche des historiques.");
        }
    }
}