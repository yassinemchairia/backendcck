package com.example.cckback.service;

import com.example.cckback.Entity.Intervention;
import com.example.cckback.Entity.Technicien;
import com.example.cckback.Repository.InterventionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class ExportService {

    @Autowired
    private InterventionRepository interventionRepository;

    public void exportInterventionsToCSV(String filePath) {
        List<Intervention> interventions = interventionRepository.findAll();

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("id,technicienId,specialite,typeIntervention,priorite,dateDebut,dureeEnHeures,nombreInterventionsPassees,heureDeLaJournee\n");

            for (Intervention intervention : interventions) {
                writer.append(String.valueOf(intervention.getIdInterv())).append(",");

                // Techniciens IDs
                StringBuilder technicienIds = new StringBuilder();
                for (Technicien technicien : intervention.getTechniciens()) {
                    technicienIds.append(technicien.getIdUser()).append(" ");
                }
                writer.append(technicienIds.toString().trim()).append(",");

                // Spécialité
                String specialite = intervention.getTechniciens().isEmpty() ? "" :
                        intervention.getTechniciens().get(0).getSpecialite().toString();
                writer.append(specialite).append(",");

                writer.append(intervention.getTypeIntervention().toString()).append(",");
                writer.append(intervention.getPriorite().toString()).append(",");

                // Gestion dateDebut null
                String dateDebutStr = (intervention.getDateDebut() != null) ?
                        intervention.getDateDebut().toString() : "";
                writer.append(dateDebutStr).append(",");

                // Durée (gestion dates nulles)
                long dureeHeures = 0;
                if (intervention.getDateDebut() != null && intervention.getDateFin() != null) {
                    dureeHeures = java.time.Duration.between(
                            intervention.getDateDebut(),
                            intervention.getDateFin()
                    ).toHours();
                }
                writer.append(String.valueOf(dureeHeures)).append(",");

                // Nombre d'interventions passées
                StringBuilder nbInterventions = new StringBuilder();
                for (Technicien technicien : intervention.getTechniciens()) {
                    nbInterventions.append(technicien.getInterventions().size()).append(" ");
                }
                writer.append(nbInterventions.toString().trim()).append(",");

                // Heure de la journée (gestion null)
                int heureDeLaJournee = (intervention.getDateDebut() != null) ?
                        intervention.getDateDebut().getHour() : 0;
                writer.append(String.valueOf(heureDeLaJournee)).append("\n");
            }

            System.out.println("✅ Fichier CSV exporté avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}