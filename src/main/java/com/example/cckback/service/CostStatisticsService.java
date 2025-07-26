package com.example.cckback.service;

import com.example.cckback.Entity.Intervention;
import com.example.cckback.Entity.RapportIntervention;
import com.example.cckback.Entity.TypeIntervention;
import com.example.cckback.Repository.InterventionRepository;
import com.example.cckback.Repository.RapportInterventionRepository;
import com.example.cckback.dto.CostStatsDTO;
import com.example.cckback.dto.TechnicienCostDTO;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CostStatisticsService {

    private final RapportInterventionRepository rapportRepository;
    private final InterventionRepository interventionRepository;

    public CostStatisticsService(RapportInterventionRepository rapportRepository,
                                 InterventionRepository interventionRepository) {
        this.rapportRepository = rapportRepository;
        this.interventionRepository = interventionRepository;
    }

    // 1. Coût total des interventions par mois
    public Map<String, Double> getMonthlyCosts(int monthsBack) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(monthsBack - 1).withDayOfMonth(1);

        List<RapportIntervention> rapports = rapportRepository.findAll()
                .stream()
                .filter(r -> r.getIntervention() != null
                        && r.getIntervention().getDateDebut() != null)
                .filter(r -> {
                    LocalDate intervDate = r.getIntervention().getDateDebut().toLocalDate();
                    return !intervDate.isBefore(startDate) && !intervDate.isAfter(endDate);
                })
                .toList();

        Map<YearMonth, Double> monthlyCosts = rapports.stream()
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getIntervention().getDateDebut()),
                        Collectors.summingDouble(RapportIntervention::getCoutIntervention)
                ));

        // Remplir les mois sans coûts
        Map<String, Double> result = new TreeMap<>();
        for (int i = 0; i < monthsBack; i++) {
            YearMonth month = YearMonth.from(startDate.plusMonths(i));
            String monthName = month.getMonth().toString() + " " + month.getYear();
            result.put(monthName, monthlyCosts.getOrDefault(month, 0.0));
        }

        return result;
    }

    // 2. Coût moyen par type d'intervention
    public Map<TypeIntervention, Double> getAverageCostByType() {
        List<Intervention> interventions = interventionRepository.findAllWithRapport();

        return interventions.stream()
                .filter(i -> i.getRapport() != null)
                .collect(Collectors.groupingBy(
                        Intervention::getTypeIntervention,
                        Collectors.averagingDouble(i -> i.getRapport().getCoutIntervention())
                ));
    }

    // 3. Version DTO plus complète
    public List<CostStatsDTO> getDetailedCostStats() {
        return interventionRepository.findAllWithRapport().stream()
                .filter(i -> i.getRapport() != null)
                .map(i -> {
                    CostStatsDTO dto = new CostStatsDTO();
                    dto.setInterventionId(i.getIdInterv());
                    dto.setTypeIntervention(i.getTypeIntervention());
                    dto.setDateIntervention(i.getDateDebut().toLocalDate());
                    dto.setCost(i.getRapport().getCoutIntervention());
                    dto.setDurationHours(Duration.between(i.getDateDebut(),
                            i.getDateFin() != null ? i.getDateFin() : LocalDateTime.now()).toHours());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    public Map<Integer, Double> getAnnualCosts(int year) {
        return rapportRepository.findAll()
                .stream()
                .filter(r -> r.getIntervention() != null
                        && r.getIntervention().getDateDebut() != null
                        && r.getIntervention().getDateDebut().getYear() == year)
                .collect(Collectors.groupingBy(
                        r -> r.getIntervention().getDateDebut().getMonthValue(),
                        TreeMap::new,
                        Collectors.summingDouble(RapportIntervention::getCoutIntervention)
                ));
    }


    public List<TechnicienCostDTO> getCostByTechnicienDetailed() {

        // 1. Récupérer toutes les interventions avec leurs rapports et techniciens
        List<Intervention> interventions = interventionRepository.findAllWithRapportAndTechniciens();

        // 2. Calculer les coûts par technicien
        Map<String, DoubleSummaryStatistics> stats = interventions.stream()
                .filter(i -> i.getRapport() != null && !i.getTechniciens().isEmpty())
                .flatMap(i -> {
                    double costPerTechnicien = i.getRapport().getCoutIntervention() / i.getTechniciens().size();
                    return i.getTechniciens().stream()
                            .map(t -> new AbstractMap.SimpleEntry<>(
                                    t.getNom() + " " + t.getPrenom(),
                                    costPerTechnicien));
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summarizingDouble(Map.Entry::getValue)
                ));

        // 3. Convertir en DTO
        return stats.entrySet().stream()
                .map(e -> new TechnicienCostDTO(
                        e.getKey(),
                        e.getValue().getSum(),
                        (int) e.getValue().getCount()))
                .sorted(Comparator.comparingDouble(TechnicienCostDTO::getTotalCost).reversed())
                .collect(Collectors.toList());
    }


    // Version simplifiée (comme dans votre demande originale)
    public Map<String, Double> getCostByTechnicien() {
        return interventionRepository.findAllWithRapportAndTechniciens()
                .stream()
                .filter(i -> i.getRapport() != null && !i.getTechniciens().isEmpty())
                .flatMap(i -> i.getTechniciens().stream()
                        .map(t -> new AbstractMap.SimpleEntry<>(
                                t.getNom() + " " + t.getPrenom(),
                                i.getRapport().getCoutIntervention() / i.getTechniciens().size())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingDouble(Map.Entry::getValue)
                ));
    }}