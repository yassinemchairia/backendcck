package com.example.cckback.service;

import com.example.cckback.Entity.Alerte;
import com.example.cckback.Entity.Intervention;
import com.example.cckback.Entity.RapportIntervention;
import com.example.cckback.Repository.AlerteRepository;
import com.example.cckback.dto.AlertessDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Service
public class AlerteExportService {

    private static final Logger LOGGER = Logger.getLogger(AlerteExportService.class.getName());

    @Autowired
    private AlerteRepository alerteRepository;

    public void exportAlertesToCSV(String filePath) {
        List<AlertessDTO> alertes = getAllAlertes();
        try {
            Files.createDirectories(Paths.get(filePath).getParent());
            boolean fileExists = Files.exists(Paths.get(filePath));
            try (FileWriter writer = new FileWriter(filePath, true)) {
                if (!fileExists || Files.size(Paths.get(filePath)) == 0) {
                    writer.append("idAlerte,typePanne,niveauGravite,valeurDeclenchement,typeCapteur,emplacement,description,solution,satisfaction\n");
                }
                for (AlertessDTO alerte : alertes) {
                    writer.append(String.valueOf(alerte.getIdAlerte())).append(",");
                    writer.append(alerte.getTypePanne()).append(",");
                    writer.append(alerte.getNiveauGravite()).append(",");
                    writer.append(String.valueOf(alerte.getValeurDeclenchement())).append(",");
                    writer.append(alerte.getTypeCapteur()).append(",");
                    writer.append(alerte.getEmplacement()).append(",");
                    writer.append(alerte.getDescription().replace(",", " ")).append(",");
                    writer.append(alerte.getSolution().replace(",", " ")).append(",");
                    writer.append(String.valueOf(alerte.getSatisfaction())).append("\n");
                }
                LOGGER.info("Fichier CSV des alertes exporté avec succès à : " + filePath);
            }
        } catch (IOException e) {
            LOGGER.severe("Erreur lors de l'exportation des alertes : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'exportation des alertes", e);
        }
    }

    public List<AlertessDTO> getAllAlertes() {
        List<Alerte> alertes = alerteRepository.findAll();
        return alertes.stream()
                .filter(alerte -> !alerte.getInterventions().isEmpty())
                .filter(alerte -> {
                    Intervention intervention = alerte.getInterventions().get(0);
                    RapportIntervention rapport = intervention.getRapport();
                    return rapport != null && rapport.getDetails() != null && !rapport.getDetails().isEmpty() && rapport.getSatisfaction() >= 1;
                })
                .map(alerte -> {
                    Intervention intervention = alerte.getInterventions().get(0);
                    RapportIntervention rapport = intervention.getRapport();
                    String solution = rapport.getDetails() != null ? rapport.getDetails().replace(",", " ") : "";
                    int satisfaction = rapport.getSatisfaction() != 0 ? rapport.getSatisfaction() : 0;
                    return new AlertessDTO(
                            alerte.getIdAlerte(),
                            alerte.getTypePanne().toString(),
                            alerte.getNiveauGravite().toString(),
                            alerte.getValeurDeclenchement(),
                            alerte.getCapteur().getType().toString(),
                            alerte.getCapteur().getEmplacement(),
                            alerte.getDescription().replace(",", " "),
                            solution,
                            satisfaction
                    );
                }).collect(Collectors.toList());
    }
}