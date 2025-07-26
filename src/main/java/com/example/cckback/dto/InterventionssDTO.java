package com.example.cckback.dto;

public class InterventionssDTO {
    private Long idInterv;
    private Long alerteId;
    private String solution;
    private Integer satisfaction;

    public InterventionssDTO(Long idInterv, Long alerteId, String solution, Integer satisfaction) {
        this.idInterv = idInterv;
        this.alerteId = alerteId;
        this.solution = solution;
        this.satisfaction = satisfaction;
    }

    public Long getIdInterv() { return idInterv; }
    public Long getAlerteId() { return alerteId; }
    public String getSolution() { return solution; }
    public Integer getSatisfaction() { return satisfaction; }
}