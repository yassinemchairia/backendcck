package com.example.cckback.service;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SatisfactionStatisticsService {

    private final RapportInterventionRepository rapportRepository;
    private final InterventionRepository interventionRepository;

    public SatisfactionStatisticsService(RapportInterventionRepository rapportRepository,
                                         InterventionRepository interventionRepository) {
        this.rapportRepository = rapportRepository;
        this.interventionRepository = interventionRepository;
    }

    // 1. Note moyenne globale de satisfaction
    public double getGlobalAverageSatisfaction() {
        return rapportRepository.findAll()
                .stream()
                .filter(r -> r.getSatisfaction() > 0)
                .mapToInt(RapportIntervention::getSatisfaction)
                .average()
                .orElse(0.0);
    }

    // 2. Évolution de la satisfaction par mois
    public Map<String, Double> getSatisfactionTrend(int monthsBack) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(monthsBack - 1).withDayOfMonth(1);

        Map<YearMonth, Double> monthlyAverages = rapportRepository.findAll()
                .stream()
                .filter(r -> r.getIntervention() != null &&
                        r.getIntervention().getDateDebut() != null &&
                        r.getSatisfaction() > 0)
                .filter(r -> {
                    LocalDate intervDate = r.getIntervention().getDateDebut().toLocalDate();
                    return !intervDate.isBefore(startDate) && !intervDate.isAfter(endDate);
                })
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getIntervention().getDateDebut()),
                        Collectors.averagingInt(RapportIntervention::getSatisfaction)
                ));

        // Remplir les mois sans données
        Map<String, Double> result = new TreeMap<>();
        for (int i = 0; i < monthsBack; i++) {
            YearMonth month = YearMonth.from(startDate.plusMonths(i));
            String monthName = month.getMonth().toString() + " " + month.getYear();
            result.put(monthName, monthlyAverages.getOrDefault(month, 0.0));
        }

        return result;
    }

    // 3. Satisfaction moyenne par type d'intervention
    public Map<TypeIntervention, Double> getAverageSatisfactionByType() {
        return interventionRepository.findAllWithRapport()
                .stream()
                .filter(i -> i.getRapport() != null && i.getRapport().getSatisfaction() > 0)
                .collect(Collectors.groupingBy(
                        Intervention::getTypeIntervention,
                        Collectors.averagingInt(i -> i.getRapport().getSatisfaction())
                ));
    }

    // 4. Détails des notes de satisfaction
    public Map<Integer, Long> getSatisfactionDistribution() {
        return rapportRepository.findAll()
                .stream()
                .filter(r -> r.getSatisfaction() > 0)
                .collect(Collectors.groupingBy(
                        RapportIntervention::getSatisfaction,
                        Collectors.counting()
                )).entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}