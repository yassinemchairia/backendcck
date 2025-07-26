package com.example.cckback.service;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.*;
import com.example.cckback.dto.CapteurStatsDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CapteurStatisticsService {

    private final CapteurRepository capteurRepository;
    private final AlerteRepository alerteRepository;

    public CapteurStatisticsService(CapteurRepository capteurRepository,
                                    AlerteRepository alerteRepository) {
        this.capteurRepository = capteurRepository;
        this.alerteRepository = alerteRepository;
    }

    // 1. Nombre d'alertes par capteur
    public Map<String, Long> countAlertesByCapteur() {
        List<Capteur> capteurs = capteurRepository.findAll();
        Map<String, Long> stats = new LinkedHashMap<>();

        capteurs.forEach(capteur -> {
            long count = alerteRepository.countByCapteur(capteur);
            stats.put(capteur.getEmplacement() + " (" + capteur.getIpAdresse() + ")", count);
        });

        return stats.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // 2. Répartition des capteurs par type
    public Map<Capteur.TypeCapteur, Long> countCapteursByType() {
        List<Capteur> capteurs = capteurRepository.findAll();
        return capteurs.stream()
                .collect(Collectors.groupingBy(
                        Capteur::getType,
                        Collectors.counting()
                ));
    }

    // 3. Capteurs les plus actifs (top 5)
    public Map<String, Long> getMostActiveCapteurs(int limit) {
        return countAlertesByCapteur().entrySet().stream()
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // Version DTO pour une meilleure structure
    public List<CapteurStatsDTO> getCapteursStats() {
        return capteurRepository.findAll().stream()
                .map(capteur -> {
                    CapteurStatsDTO dto = new CapteurStatsDTO();
                    dto.setCapteurId(capteur.getIdCapt());
                    dto.setEmplacement(capteur.getEmplacement());
                    dto.setIpAdresse(capteur.getIpAdresse());
                    dto.setType(capteur.getType());
                    dto.setNombreAlertes(alerteRepository.countByCapteur(capteur));
                    return dto;
                })
                .sorted(Comparator.comparingLong(CapteurStatsDTO::getNombreAlertes).reversed())
                .collect(Collectors.toList());
    }
    public List<CapteurStatsDTO> getCapteursStatsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return capteurRepository.findAll().stream()
                .map(capteur -> {
                    CapteurStatsDTO dto = new CapteurStatsDTO();
                    dto.setCapteurId(capteur.getIdCapt());
                    dto.setEmplacement(capteur.getEmplacement());
                    dto.setIpAdresse(capteur.getIpAdresse());
                    dto.setType(capteur.getType());

                    long alertesCount = alerteRepository.countByCapteurAndDateAlerteBetween(
                            capteur,
                            startDate.atStartOfDay(),
                            endDate.atTime(23, 59, 59));

                    dto.setNombreAlertes(alertesCount);
                    return dto;
                })
                .sorted(Comparator.comparingLong(CapteurStatsDTO::getNombreAlertes).reversed())
                .collect(Collectors.toList());
    }
}