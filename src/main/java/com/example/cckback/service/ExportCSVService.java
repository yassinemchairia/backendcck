package com.example.cckback.service;

import com.example.cckback.Entity.Intervention;
import com.example.cckback.Entity.Technicien;
import com.example.cckback.Repository.InterventionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportCSVService {
@Autowired
    private  InterventionRepository interventionRepository;

    @Transactional
    public void exportToCSV(String filePath) {
        List<Intervention> interventions = interventionRepository.findAll();

        try (FileWriter writer = new FileWriter(filePath)) {
            // En-têtes CSV
            writer.write("id,technicienId,specialite,typeIntervention,priorite,dateDebut,dureeEnHeures,nombreInterventionsPassees\n");

            for (Intervention i : interventions) {
                if (i.getStatut() == null || i.getStatut().name().equals("EN_COURS") || i.getDateFin() == null) continue;

                for (Technicien t : i.getTechniciens()) {
                    long duree = Duration.between(i.getDateDebut(), i.getDateFin()).toHours();
                    int nbInterventions = t.getInterventions().size() - 1; // on exclut l'actuelle

                    writer.write(String.format(
                            "%d,%d,%s,%s,%s,%s,%d,%d\n",
                            i.getIdInterv(),
                            t.getIdUser(),
                            t.getSpecialite(),
                            i.getTypeIntervention(),
                            i.getPriorite(),
                            i.getDateDebut().toLocalDate(),
                            duree,
                            nbInterventions < 0 ? 0 : nbInterventions
                    ));
                }
            }

            System.out.println("✅ CSV généré avec succès : " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}