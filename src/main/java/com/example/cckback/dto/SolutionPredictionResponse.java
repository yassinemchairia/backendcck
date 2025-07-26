package com.example.cckback.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolutionPredictionResponse {
    private String solution;

    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }
}