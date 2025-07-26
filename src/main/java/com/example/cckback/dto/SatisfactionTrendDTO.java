package com.example.cckback.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
public class SatisfactionTrendDTO {
    private YearMonth period;
    private double averageSatisfaction;
    private int interventionCount;

    public SatisfactionTrendDTO(YearMonth period, double averageSatisfaction, int interventionCount) {
        this.period = period;
        this.averageSatisfaction = averageSatisfaction;
        this.interventionCount = interventionCount;
    }

    public double getAverageSatisfaction() {
        return averageSatisfaction;
    }
    public int getInterventionCount() {
        return interventionCount;
    }
    public YearMonth getPeriod() {
        return period;
    }

}
