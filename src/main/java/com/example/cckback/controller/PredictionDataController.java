package com.example.cckback.controller;

import com.example.cckback.Entity.Intervention;
import com.example.cckback.Entity.Technicien;
import com.example.cckback.Repository.InterventionRepository;
import com.example.cckback.dto.PredictionDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/prediction")
public class PredictionDataController {

    @Autowired
    private InterventionRepository interventionRepository;

    @GetMapping("/donnees")
    public List<PredictionDataDTO> getDonneesPrediction() {
        List<Intervention> interventions = interventionRepository.findAll();
        List<PredictionDataDTO> result = new ArrayList<>();

        for (Intervention intervention : interventions) {
            List<Technicien> techniciens = intervention.getTechniciens();

            for (Technicien tech : techniciens) {
                int nbInterventionsPassees = tech.getInterventions().size();
                int nbTechniciens = techniciens.size();
                int dureeEnHeures = 0;

                if (intervention.getDateDebut() != null && intervention.getDateFin() != null) {
                    dureeEnHeures = (int) java.time.Duration.between(
                            intervention.getDateDebut(),
                            intervention.getDateFin()
                    ).toHours();
                }

                PredictionDataDTO dto = new PredictionDataDTO(
                        intervention.getIdInterv(),
                        Math.toIntExact(tech.getIdUser()),
                        tech.getSpecialite().name(),
                        intervention.getTypeIntervention().name(),
                        intervention.getPriorite().name(),
                        intervention.getDateDebut(),
                        dureeEnHeures,
                        nbInterventionsPassees,
                        nbTechniciens,
                        intervention.getPriorite().name() // ou autre logique
                );

                result.add(dto);
            }
        }

        return result;
    }}