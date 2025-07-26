package com.example.cckback.service;


import com.example.cckback.Entity.CalendrierDisponibilite;
import com.example.cckback.Entity.Specialite;
import com.example.cckback.Entity.Technicien;
import com.example.cckback.Repository.CalendrierDisponibiliteRepository;
import com.example.cckback.Repository.TechnicienRepository;
import com.example.cckback.dto.TechniciensDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CalendrierDisponibiliteService {

    @Autowired
    private CalendrierDisponibiliteRepository calendrierDisponibiliteRepository;

    @Autowired
    private TechnicienRepository technicienRepository;
    private TechniciensDTO convertToDTO(Technicien technicien) {
        return new TechniciensDTO(
                technicien.getIdUser(),
                technicien.getEmail(),
                technicien.getNom(),
                technicien.isValide(),
                technicien.getSpecialite(),
                technicien.getNumeroTelephone(),
                technicien.getDateDisponibilite()
        );
    }
    public CalendrierDisponibilite ajouterDisponibilite(Long technicienId, LocalDate date, boolean disponible) {
        Optional<Technicien> technicienOpt = technicienRepository.findById(technicienId);

        if (technicienOpt.isEmpty()) {
            throw new TechnicienNotFoundException("Technicien non trouvé.");
        }

        Technicien technicien = technicienOpt.get();

        List<CalendrierDisponibilite> existante = calendrierDisponibiliteRepository.findByTechnicienAndDate(technicien, date);
        if (!existante.isEmpty()) {
            throw new RuntimeException("Le technicien a déjà une disponibilité définie pour cette date.");
        }

        CalendrierDisponibilite calendrier = new CalendrierDisponibilite();
        calendrier.setTechnicien(technicien);
        calendrier.setDate(date);
        calendrier.setDisponible(disponible);

        calendrier = calendrierDisponibiliteRepository.save(calendrier);
        calendrier.setTechnicien(technicienRepository.findById(technicien.getIdUser()).get());

        return calendrier;
    }

    public List<CalendrierDisponibilite> getDisponibilitesParTechnicien(Long technicienId) {
        Optional<Technicien> technicienOpt = technicienRepository.findById(technicienId);
        if (technicienOpt.isPresent()) {
            return calendrierDisponibiliteRepository.findByTechnicien(technicienOpt.get());
        } else {
            throw new RuntimeException("Technicien non trouvé.");
        }
    }

    public List<TechniciensDTO> getTechniciensDisponiblesPourDate(LocalDate date) {
        List<CalendrierDisponibilite> disponibilites = calendrierDisponibiliteRepository.findByDate(date);
        return disponibilites.stream()
                .filter(CalendrierDisponibilite::isDisponible)
                .map(calendrier -> {
                    Technicien tech = calendrier.getTechnicien();
                    tech.setDateDisponibilite(calendrier.getDate());
                    return convertToDTO(tech);
                })
                .collect(Collectors.toList());
    }

    public List<TechniciensDTO> findTechniciensDisponiblesEntre(LocalDate start, LocalDate end) {
        List<CalendrierDisponibilite> disponibilites = calendrierDisponibiliteRepository.findByDateBetween(start, end);
        List<TechniciensDTO> techniciens = new ArrayList<>();
        for (CalendrierDisponibilite dispo : disponibilites) {
            Technicien t = dispo.getTechnicien();
            t.setDateDisponibilite(dispo.getDate());
            techniciens.add(convertToDTO(t));
        }
        return techniciens;
    }

    public List<TechniciensDTO> getTechniciensDisponiblesParIntervalle(LocalDate dateDebut, LocalDate dateFin, List<Long> techniciensDejaAffiches) {
        List<CalendrierDisponibilite> disponibilites = calendrierDisponibiliteRepository.findByDateBetween(dateDebut, dateFin);
        List<TechniciensDTO> techniciensDisponibles = new ArrayList<>();
        for (CalendrierDisponibilite calendrier : disponibilites) {
            if (calendrier.isDisponible()) {
                Technicien technicien = calendrier.getTechnicien();
                if (!techniciensDejaAffiches.contains(technicien.getIdUser())) {
                    technicien.setDateDisponibilite(calendrier.getDate());
                    techniciensDisponibles.add(convertToDTO(technicien));
                    techniciensDejaAffiches.add(technicien.getIdUser());
                }
            }
        }
        return techniciensDisponibles;
    }

    public List<TechniciensDTO> getTechniciensDisponiblesParIntervalleEtSpecialite(LocalDate dateDebut, LocalDate dateFin, Specialite specialite, List<Long> techniciensDejaAffiches) {
        List<CalendrierDisponibilite> disponibilites = calendrierDisponibiliteRepository.findByDateBetween(dateDebut, dateFin);
        List<TechniciensDTO> techniciensDisponibles = new ArrayList<>();
        for (CalendrierDisponibilite calendrier : disponibilites) {
            if (calendrier.isDisponible() && calendrier.getTechnicien().getSpecialite() == specialite) {
                Technicien technicien = calendrier.getTechnicien();
                technicien.setDateDisponibilite(calendrier.getDate());
                if (!techniciensDejaAffiches.contains(technicien.getIdUser())) {
                    techniciensDisponibles.add(convertToDTO(technicien));
                    techniciensDejaAffiches.add(technicien.getIdUser());
                }
            }
        }
        return techniciensDisponibles;
    }
    public Optional<CalendrierDisponibilite> findByTechnicienIdAndDate(Long technicienId, LocalDate date) {
        Optional<Technicien> technicienOpt = technicienRepository.findById(technicienId);
        if (technicienOpt.isEmpty()) {
            throw new TechnicienNotFoundException("Technicien non trouvé.");
        }
        return calendrierDisponibiliteRepository.findByTechnicienAndDate(technicienOpt.get(), date).stream().findFirst();
    }
    public void deleteDisponibilite(CalendrierDisponibilite disponibilite) {
        calendrierDisponibiliteRepository.delete(disponibilite);
    }
    public List<CalendrierDisponibilite> getNonDisponibilitesParTechnicien(Long technicienId, LocalDate start, LocalDate end) {
        Optional<Technicien> technicienOpt = technicienRepository.findById(technicienId);
        if (technicienOpt.isEmpty()) {
            throw new TechnicienNotFoundException("Technicien non trouvé.");
        }
        return calendrierDisponibiliteRepository.findByTechnicienAndDateBetweenAndDisponibleFalse(
                technicienOpt.get(), start, end);
    }
    public List<CalendrierDisponibilite> ajouterDisponibilitesEnMasse(Long technicienId, LocalDate dateDebut, LocalDate dateFin, boolean disponible) {
        Optional<Technicien> technicienOpt = technicienRepository.findById(technicienId);
        if (technicienOpt.isEmpty()) {
            throw new TechnicienNotFoundException("Technicien non trouvé.");
        }
        Technicien technicien = technicienOpt.get();
        List<CalendrierDisponibilite> nouvellesDisponibilites = new ArrayList<>();
        LocalDate currentDate = dateDebut;
        while (!currentDate.isAfter(dateFin)) {
            List<CalendrierDisponibilite> existante = calendrierDisponibiliteRepository.findByTechnicienAndDate(technicien, currentDate);
            if (!existante.isEmpty()) {
                throw new RuntimeException("Une disponibilité existe déjà pour le " + currentDate);
            }
            CalendrierDisponibilite disponibilite = new CalendrierDisponibilite();
            disponibilite.setTechnicien(technicien);
            disponibilite.setDate(currentDate);
            disponibilite.setDisponible(disponible);
            nouvellesDisponibilites.add(calendrierDisponibiliteRepository.save(disponibilite));
            currentDate = currentDate.plusDays(1);
        }
        return nouvellesDisponibilites;
    }
    public CalendrierDisponibilite save(CalendrierDisponibilite disponibilite) {
        return calendrierDisponibiliteRepository.save(disponibilite);
    }
}

