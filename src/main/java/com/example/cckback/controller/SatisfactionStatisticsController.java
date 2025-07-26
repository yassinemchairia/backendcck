package com.example.cckback.controller;

import com.example.cckback.Entity.TypeIntervention;
import com.example.cckback.service.SatisfactionStatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics/satisfaction")
public class SatisfactionStatisticsController {

    private final SatisfactionStatisticsService satisfactionService;

    public SatisfactionStatisticsController(SatisfactionStatisticsService satisfactionService) {
        this.satisfactionService = satisfactionService;
    }

    @GetMapping("/average")
    public double getGlobalAverageSatisfaction() {
        return satisfactionService.getGlobalAverageSatisfaction();
    }

    @GetMapping("/trend")
    public Map<String, Double> getSatisfactionTrend(
            @RequestParam(defaultValue = "6") int monthsBack) {
        return satisfactionService.getSatisfactionTrend(monthsBack);
    }

    @GetMapping("/by-type")
    public Map<TypeIntervention, Double> getAverageSatisfactionByType() {
        return satisfactionService.getAverageSatisfactionByType();
    }

    @GetMapping("/distribution")
    public Map<Integer, Long> getSatisfactionDistribution() {
        return satisfactionService.getSatisfactionDistribution();
    }}
