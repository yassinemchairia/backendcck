package com.example.cckback.service;

import com.example.cckback.Repository.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimeStatisticsService {

    private final AlerteRepository alerteRepository;
    private final InterventionRepository interventionRepository;

    public TimeStatisticsService(AlerteRepository alerteRepository,
                                 InterventionRepository interventionRepository) {
        this.alerteRepository = alerteRepository;
        this.interventionRepository = interventionRepository;
    }

    // 1. Nombre d'alertes par jour/mois
    public Map<String, Long> getAlertCountByTimePeriod(ChronoUnit unit, int periodCount) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(periodCount, unit);

        return alerteRepository.findByDateAlerteBetween(startDate, endDate)
                .stream()
                .collect(Collectors.groupingBy(
                        alerte -> formatDate(alerte.getDateAlerte(), unit),
                        TreeMap::new,  // Trie par date
                        Collectors.counting()
                ));
    }

    // 2. Nombre d'interventions par jour/mois
    public Map<String, Long> getInterventionCountByTimePeriod(ChronoUnit unit, int periodCount) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(periodCount, unit);

        return interventionRepository.findByDateDebutBetween(startDate, endDate)
                .stream()
                .collect(Collectors.groupingBy(
                        interv -> formatDate(interv.getDateDebut(), unit),
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    // 3. Heures les plus critiques pour les pannes
    public Map<Integer, Long> getCriticalHours() {
        return alerteRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        alerte -> alerte.getDateAlerte().getHour(),
                        Collectors.counting()
                )).entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // 4. Jours de la semaine les plus critiques
    public Map<DayOfWeek, Long> getCriticalDays() {
        return alerteRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        alerte -> alerte.getDateAlerte().getDayOfWeek(),
                        Collectors.counting()
                )).entrySet().stream()
                .sorted(Map.Entry.<DayOfWeek, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private String formatDate(LocalDateTime date, ChronoUnit unit) {
        if (unit == ChronoUnit.DAYS) {
            return date.toLocalDate().toString();
        } else if (unit == ChronoUnit.MONTHS) {
            return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
        }
        throw new IllegalArgumentException("Unité de temps non supportée: " + unit);
    }
}