package com.example.cckback.controller;

import com.example.cckback.Entity.Capteur;
import com.example.cckback.dto.CapteurStatsDTO;
import com.example.cckback.service.CapteurStatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics/capteurs")
public class CapteurStatisticsController {

    private final CapteurStatisticsService capteurStatisticsService;

    public CapteurStatisticsController(CapteurStatisticsService capteurStatisticsService) {
        this.capteurStatisticsService = capteurStatisticsService;
    }

    @GetMapping("/alertes-par-capteur")
    public Map<String, Long> getAlertesByCapteur() {
        return capteurStatisticsService.countAlertesByCapteur();
    }

    @GetMapping("/repartition-types")
    public Map<Capteur.TypeCapteur, Long> getRepartitionByType() {
        return capteurStatisticsService.countCapteursByType();
    }

    @GetMapping("/capteurs-actifs")
    public Map<String, Long> getMostActiveCapteurs(
            @RequestParam(defaultValue = "5") int limit) {
        return capteurStatisticsService.getMostActiveCapteurs(limit);
    }

    @GetMapping("/full-stats")
    public List<CapteurStatsDTO> getFullCapteursStats() {
        return capteurStatisticsService.getCapteursStats();
    }
    @GetMapping("/full-stats/Between")
    public List<CapteurStatsDTO> getFullCapteursStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate != null && endDate != null) {
            return capteurStatisticsService.getCapteursStatsBetweenDates(startDate, endDate);
        }
        return capteurStatisticsService.getCapteursStats();
    }
}
