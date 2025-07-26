package com.example.cckback.controller;

import com.example.cckback.Entity.Capteur;
import com.example.cckback.service.SurveillanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/capteurs")
public class CapteurController {

    @Autowired
    private SurveillanceService surveillanceService;

    @GetMapping
    public ResponseEntity<List<Capteur>> getAllCapteurs() {
        try {
            List<Capteur> capteurs = surveillanceService.getCapteurRepository().findAll();
            return new ResponseEntity<>(capteurs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Capteur> getCapteurById(@PathVariable Long id) {
        try {
            Capteur capteur = surveillanceService.getCapteurRepository().findById(id)
                    .orElseThrow(() -> new RuntimeException("Capteur non trouvé avec l'ID : " + id));
            return new ResponseEntity<>(capteur, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Capteur> updateCapteur(@PathVariable Long id, @RequestBody Capteur capteurDetails) {
        try {
            Capteur capteur = surveillanceService.getCapteurRepository().findById(id)
                    .orElseThrow(() -> new RuntimeException("Capteur non trouvé avec l'ID : " + id));
            capteur.setIpAdresse(capteurDetails.getIpAdresse());
            capteur.setEmplacement(capteurDetails.getEmplacement());
            capteur.setDepartement(capteurDetails.getDepartement());
            capteur.setType(capteurDetails.getType());
            capteur.setUniteMesure(capteurDetails.getUniteMesure());
            // Ne mettez pas à jour valeurActuelle ou etatElectricite ici, laissez SurveillanceService le gérer
            Capteur updatedCapteur = surveillanceService.getCapteurRepository().save(capteur);
            return new ResponseEntity<>(updatedCapteur, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Capteur>> getCapteursByType(@PathVariable Capteur.TypeCapteur type) {
        try {
            List<Capteur> capteurs = surveillanceService.getCapteurRepository().findByType(type);
            if (capteurs.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(capteurs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}