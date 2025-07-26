package com.example.cckback.controller;
import com.example.cckback.service.ExportCSVService;
import com.example.cckback.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @GetMapping("/csv")
    public String exportCSV() {
        exportService.exportInterventionsToCSV("interventions.csv"); // Le fichier sera généré dans le dossier racine du projet
        return "Export terminé !";
    }
}