package com.example.cckback.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RendezVousDTO {
    private Long id;
    private String description;
    private LocalDateTime dateRendezVous;
    private boolean notificationEnvoyee;
    private Long adminId;
    private List<Long> technicienIds;

    public void setDateRendezVous(LocalDateTime dateRendezVous) {
        this.dateRendezVous = dateRendezVous;
    }

    public void setNotificationEnvoyee(boolean notificationEnvoyee) {
        this.notificationEnvoyee = notificationEnvoyee;
    }

    public boolean isNotificationEnvoyee() {
        return notificationEnvoyee;
    }

    public LocalDateTime getDateRendezVous() {
        return dateRendezVous;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Long getAdminId() {
        return adminId;
    }
    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
    public List<Long> getTechnicienIds() {
        return technicienIds;
    }
    public void setTechnicienIds(List<Long> technicienIds) {
        this.technicienIds = technicienIds;
    }

}