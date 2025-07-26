package com.example.cckback.controller;

import com.example.cckback.service.TimeStatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics/time")
public class TimeStatisticsController {

    private final TimeStatisticsService timeStatisticsService;

    public TimeStatisticsController(TimeStatisticsService timeStatisticsService) {
        this.timeStatisticsService = timeStatisticsService;
    }

    @GetMapping("/alertes")
    public Map<String, Long> getAlertCountByPeriod(
            @RequestParam(defaultValue = "DAYS") ChronoUnit unit,
            @RequestParam(defaultValue = "30") int periodCount) {
        return timeStatisticsService.getAlertCountByTimePeriod(unit, periodCount);
    }

    @GetMapping("/interventions")
    public Map<String, Long> getInterventionCountByPeriod(
            @RequestParam(defaultValue = "DAYS") ChronoUnit unit,
            @RequestParam(defaultValue = "30") int periodCount) {
        return timeStatisticsService.getInterventionCountByTimePeriod(unit, periodCount);
    }

    @GetMapping("/heures-critiques")
    public Map<Integer, Long> getCriticalHours() {
        return timeStatisticsService.getCriticalHours();
    }

    @GetMapping("/jours-critiques")
    public Map<String, Long> getCriticalDays() {
        return timeStatisticsService.getCriticalDays().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        Map.Entry::getValue
                ));
    }
}