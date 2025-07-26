package com.example.cckback.controller;

import com.example.cckback.dto.AlertessDTO;
import com.example.cckback.service.AlerteExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alertes")
public class AlerteExportController {

    @Autowired
    private AlerteExportService alerteExportService;

    @GetMapping("/export")
    public ResponseEntity<List<AlertessDTO>> exportAlertes() {
        String filePath = "C:/Users/mchai/IdeaProjects/cckback/alertes.csv"; // Chemin dans le projet Spring Boot
        alerteExportService.exportAlertesToCSV(filePath);
        List<AlertessDTO> alertes = alerteExportService.getAllAlertes();
        return ResponseEntity.ok(alertes);
    }
}