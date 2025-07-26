package com.example.cckback.dto;

import java.util.List;

public class RendezVousRequest {
    private Long adminId;
    private String description;
    private String date;  // au format ISO 8601
    private List<Long> technicienIds;

    // Getters and Setters
    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Long> getTechnicienIds() {
        return technicienIds;
    }

    public void setTechnicienIds(List<Long> technicienIds) {
        this.technicienIds = technicienIds;
    }
}