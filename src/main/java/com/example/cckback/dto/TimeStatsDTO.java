package com.example.cckback.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.Map;

@Getter
@Setter
public class TimeStatsDTO {
    private Map<String, Long> alertesParPeriode;
    private Map<String, Long> interventionsParPeriode;
    private Map<Integer, Long> heuresCritiques;
    private Map<DayOfWeek, Long> joursCritiques;




}
