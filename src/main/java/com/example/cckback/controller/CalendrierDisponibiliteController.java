package com.example.cckback.controller;

import com.example.cckback.Entity.CalendrierDisponibilite;
import com.example.cckback.Entity.Specialite;
import com.example.cckback.Entity.Technicien;
import com.example.cckback.dto.TechniciensDTO;
import com.example.cckback.service.CalendrierDisponibiliteService;
import com.example.cckback.service.TechnicienNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 // Assurez-vous d'importer l'exception

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/disponibilites")
public class CalendrierDisponibiliteController {

    @Autowired
    private CalendrierDisponibiliteService calendrierDisponibiliteService;

    // ✅ Endpoint pour ajouter une disponibilité avec JSON en body
    @PostMapping("/ajouter")
    public ResponseEntity<?> ajouterDisponibilite(@RequestBody DisponibiliteRequest request) {
        try {
            CalendrierDisponibilite disponibilite = calendrierDisponibiliteService.ajouterDisponibilite(
                    request.getTechnicienId(),
                    request.getDate(),
                    request.isDisponible()
            );
            return ResponseEntity.ok(disponibilite);
        } catch (TechnicienNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Technicien non trouvé.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Une erreur s'est produite : " + e.getMessage());
        }
    }

    @GetMapping("/{technicienId}")
    public ResponseEntity<List<CalendrierDisponibilite>> getDisponibilitesParTechnicien(@PathVariable Long technicienId) {
        List<CalendrierDisponibilite> disponibilites = calendrierDisponibiliteService.getDisponibilitesParTechnicien(technicienId);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/techniciens-disponibles")
    public ResponseEntity<List<TechniciensDTO>> getTechniciensDisponiblesPourDate(@RequestParam("date") LocalDate date) {
        List<TechniciensDTO> techniciensDisponibles = calendrierDisponibiliteService.getTechniciensDisponiblesPourDate(date);
        return ResponseEntity.ok(techniciensDisponibles);
    }

    @GetMapping("/techniciens-disponibles-par-periode")
    public ResponseEntity<List<TechniciensDTO>> getTechniciensDisponiblesParPeriode(
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<TechniciensDTO> techniciens = calendrierDisponibiliteService.findTechniciensDisponiblesEntre(start, end);
        return ResponseEntity.ok(techniciens);
    }

    @GetMapping("/intervalle")
    public ResponseEntity<List<TechniciensDTO>> getTechniciensDisponiblesParIntervalle(
            @RequestParam("dateDebut") String dateDebutStr,
            @RequestParam("dateFin") String dateFinStr,
            @RequestParam(value = "techniciensDejaAffiches", required = false) List<Long> techniciensDejaAffiches) {
        LocalDate dateDebut = LocalDate.parse(dateDebutStr);
        LocalDate dateFin = LocalDate.parse(dateFinStr);
        if (techniciensDejaAffiches == null) {
            techniciensDejaAffiches = new ArrayList<>();
        }
        List<TechniciensDTO> techniciensDisponibles = calendrierDisponibiliteService.getTechniciensDisponiblesParIntervalle(dateDebut, dateFin, techniciensDejaAffiches);
        return ResponseEntity.ok(techniciensDisponibles);
    }

    @GetMapping("/intervalle-et-specialite")
    public ResponseEntity<List<TechniciensDTO>> getTechniciensDisponiblesParIntervalleEtSpecialite(
            @RequestParam("dateDebut") String dateDebutStr,
            @RequestParam("dateFin") String dateFinStr,
            @RequestParam("specialite") Specialite specialite,
            @RequestParam(value = "techniciensDejaAffiches", required = false) List<Long> techniciensDejaAffiches) {
        LocalDate dateDebut = LocalDate.parse(dateDebutStr);
        LocalDate dateFin = LocalDate.parse(dateFinStr);
        if (techniciensDejaAffiches == null) {
            techniciensDejaAffiches = new ArrayList<>();
        }
        List<TechniciensDTO> techniciensDisponibles = calendrierDisponibiliteService.getTechniciensDisponiblesParIntervalleEtSpecialite(dateDebut, dateFin, specialite, techniciensDejaAffiches);
        return ResponseEntity.ok(techniciensDisponibles);
    }

    public static class DisponibiliteRequest {
        private Long technicienId;
        private LocalDate date;
        private boolean disponible;

        public Long getTechnicienId() { return technicienId; }
        public void setTechnicienId(Long technicienId) { this.technicienId = technicienId; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public boolean isDisponible() { return disponible; }
        public void setDisponible(boolean disponible) { this.disponible = disponible; }
    }

    @PutMapping("/modifier")
    public ResponseEntity<?> modifierDisponibilite(@RequestBody DisponibiliteRequest request) {
        try {
            Optional<CalendrierDisponibilite> disponibiliteOpt = calendrierDisponibiliteService.findByTechnicienIdAndDate(
                    request.getTechnicienId(), request.getDate());
            if (disponibiliteOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Aucune disponibilité trouvée pour ce technicien à cette date.");
            }
            CalendrierDisponibilite disponibilite = disponibiliteOpt.get();
            disponibilite.setDisponible(request.isDisponible());
            calendrierDisponibiliteService.save(disponibilite);
            return ResponseEntity.ok(disponibilite);
        } catch (TechnicienNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Technicien non trouvé.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur : " + e.getMessage());
        }
    }
    @DeleteMapping("/supprimer")
    public ResponseEntity<?> supprimerDisponibilite(
            @RequestParam Long technicienId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) { // <-- CHANGE THIS LINE
        try {
            // No need for LocalDate.parse(date) here anymore, Spring handles it
            Optional<CalendrierDisponibilite> disponibiliteOpt = calendrierDisponibiliteService.findByTechnicienIdAndDate(technicienId, date);
            if (disponibiliteOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Aucune disponibilité trouvée pour ce technicien à cette date.");
            }
            calendrierDisponibiliteService.deleteDisponibilite(disponibiliteOpt.get());
            return ResponseEntity.noContent().build();
        } catch (TechnicienNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Technicien non trouvé.");
        } catch (Exception e) {
            // It's good practice to log the full exception
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur : " + e.getMessage());
        }
    }
    @GetMapping("/{technicienId}/non-disponibles")
    public ResponseEntity<List<CalendrierDisponibilite>> getJoursNonDisponibles(
            @PathVariable Long technicienId,
            @RequestParam String dateDebut,
            @RequestParam String dateFin) {
        try {
            LocalDate start = LocalDate.parse(dateDebut);
            LocalDate end = LocalDate.parse(dateFin);
            List<CalendrierDisponibilite> nonDisponibilites = calendrierDisponibiliteService
                    .getNonDisponibilitesParTechnicien(technicienId, start, end);
            return ResponseEntity.ok(nonDisponibilites);
        } catch (TechnicienNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PostMapping("/masse")
    public ResponseEntity<?> ajouterDisponibilitesEnMasse(@RequestBody DisponibiliteMasseRequest request) {
        try {
            List<CalendrierDisponibilite> disponibilites = calendrierDisponibiliteService.ajouterDisponibilitesEnMasse(
                    request.getTechnicienId(), request.getDateDebut(), request.getDateFin(), request.isDisponible());
            return ResponseEntity.ok(disponibilites);
        } catch (TechnicienNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Technicien non trouvé.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur : " + e.getMessage());
        }
    }

    public static class DisponibiliteMasseRequest {
        private Long technicienId;
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private boolean disponible;

        public Long getTechnicienId() {
            return technicienId;
        }
        public void setTechnicienId(Long technicienId) {
            this.technicienId = technicienId;

        }
        public LocalDate getDateDebut() {
            return dateDebut;
        }
        public void setDateDebut(LocalDate dateDebut) {
            this.dateDebut = dateDebut;

        }
        public LocalDate getDateFin() {
            return dateFin;
        }
        public void setDateFin(LocalDate dateFin) {
            this.dateFin = dateFin;
        }
        public boolean isDisponible() {
            return disponible;
        }
        public void setDisponible(boolean disponible) {
            this.disponible = disponible;
        }


        // Getters et setters
    }
}