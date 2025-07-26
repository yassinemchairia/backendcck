package com.example.cckback.service;

import com.example.cckback.Entity.*;
import com.example.cckback.dto.TechnicienStatsDTO;
import com.example.cckback.Repository.TechnicienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TechnicienStatsService {
    @Autowired
    private  TechnicienRepository technicienRepository;

    public TechnicienStatsDTO getStatsForTechnicien(Long idTech) {
        Technicien technicien = technicienRepository.findById(idTech)
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé"));

        List<Intervention> interventions = technicien.getInterventions();

        int nbInterventions = interventions.size();
        long totalMinutes = 0;
        int terminees = 0;

        Map<String, Integer> parPriorite = new HashMap<>();
        Map<String, Integer> parType = new HashMap<>();

        for (Intervention i : interventions) {
            if (i.getDateDebut() != null && i.getDateFin() != null) {
                Duration duration = Duration.between(i.getDateDebut(), i.getDateFin());
                totalMinutes += duration.toMinutes();
            }

            if (i.getStatut() == Statut.TERMINEE) {
                terminees++;
            }

            if (i.getPriorite() != null) {
                parPriorite.merge(i.getPriorite().name(), 1, Integer::sum);
            }

            if (i.getTypeIntervention() != null) {
                parType.merge(i.getTypeIntervention().name(), 1, Integer::sum);
            }
        }

        long dureeMoyenne = nbInterventions > 0 ? totalMinutes / nbInterventions : 0;
        String tauxReussite = nbInterventions > 0 ? (terminees * 100 / nbInterventions) + "%" : "0%";

        TechnicienStatsDTO dto = new TechnicienStatsDTO();
        dto.setIdUser(technicien.getIdUser());
        dto.setNom(technicien.getNom());
        dto.setPrenom(technicien.getPrenom());
        dto.setSpecialite(technicien.getSpecialite().name());
        dto.setNbInterventions(nbInterventions);
        dto.setDureeTotale((totalMinutes / 60) + "h");
        dto.setDureeMoyenne((dureeMoyenne / 60) + "h");
        dto.setTauxReussite(tauxReussite);
        dto.setStatsParPriorite(parPriorite);
        dto.setStatsParType(parType);

        return dto;
    }
}