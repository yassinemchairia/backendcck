package com.example.cckback.controller;

import com.example.cckback.Entity.TypeIntervention;
import com.example.cckback.dto.CostStatsDTO;
import com.example.cckback.dto.TechnicienCostDTO;
import com.example.cckback.service.CostStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics/costs")
public class CostStatisticsController {

    private final CostStatisticsService costStatisticsService;

    public CostStatisticsController(CostStatisticsService costStatisticsService) {
        this.costStatisticsService = costStatisticsService;
    }

    @GetMapping("/monthly")
    public Map<String, Double> getMonthlyCosts(
            @RequestParam(defaultValue = "12") int monthsBack) {
        return costStatisticsService.getMonthlyCosts(monthsBack);
    }

    @GetMapping("/average-by-type")
    public Map<TypeIntervention, Double> getAverageCostByType() {
        return costStatisticsService.getAverageCostByType();
    }

    @GetMapping("/detailed")
    public List<CostStatsDTO> getDetailedCostStats() {
        return costStatisticsService.getDetailedCostStats();
    }
    @GetMapping("/annual")
    public Map<Integer, Double> getAnnualCosts(
            @RequestParam(required = false) Integer year) {

        if (year == null) {
            year = LocalDate.now().getYear();
        }

        return costStatisticsService.getAnnualCosts(year);
    }
    @GetMapping("/by-technicien")
    public Map<String, Double> getCostByTechnicien() {
        return costStatisticsService.getCostByTechnicien();
    }

    @GetMapping("/by-technicien/detailed")
    public List<TechnicienCostDTO> getDetailedCostByTechnicien() {
        return costStatisticsService.getCostByTechnicienDetailed();
    }
}