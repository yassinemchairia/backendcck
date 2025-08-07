package com.example.cckback.service;



import com.example.cckback.Entity.Intervention;
import com.example.cckback.Entity.Role;
import com.example.cckback.Entity.Specialite;
import com.example.cckback.Entity.Technicien;
import com.example.cckback.Repository.*;
import com.example.cckback.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TechnicienService {

    @Autowired
    private TechnicienRepository technicienRepository;
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private InterventionRepository interventionRepository;
    @Autowired
    private CalendrierDisponibiliteRepository disponibiliteRepository;
    @Autowired
    private RapportInterventionRepository rapportRepository;
    @Value("${flask.api.technician.url}")
    private String flaskApiUrl;

    @Autowired
    private RestTemplate restTemplate;
    public int predictTechnicien(PredictionRequest request) {
        try {
            ResponseEntity<PredictionResponse> response = restTemplate.postForEntity(
                    flaskApiUrl + "/predict", request, PredictionResponse.class
            );
            return response.getBody().getTechnicienId();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public List<TechnicienDTO> getAllTechniciens() {
        return technicienRepository.findAll().stream()
                .map(technicien -> new TechnicienDTO(
                        technicien.getIdUser(),
                        technicien.getNom(),
                        technicien.getPrenom(),
                        technicien.getSpecialite()))
                .collect(Collectors.toList());
    }

    public TechnicienService(TechnicienRepository technicienRepository,UserRepository userRepository,RestTemplate restTemplate) {
        this.technicienRepository = technicienRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public List<Technicien> getTechniciensParSpecialite(Specialite specialite) {
        return technicienRepository.findBySpecialite(specialite);
    }
    public List<Technicien> getTechniciensNonValides() {
        // Requête pour obtenir tous les utilisateurs non validés avec le rôle "TECHNICIEN"
        List<Technicien> techniciensNonValides = userRepository.findByRoleAndValide(Role.TECHNICIEN, false)
                .stream()
                .filter(user -> user instanceof Technicien) // Filtrer pour ne garder que les Techniciens
                .map(user -> (Technicien) user) // Cast de l'utilisateur en Technicien
                .collect(Collectors.toList()); // Retourner une liste de Technicien
        return techniciensNonValides;
    }

    // 1. Nombre d'interventions par technicien
    public Map<String, Long> countInterventionsByTechnicien() {
        List<Technicien> techniciens = technicienRepository.findAll();
        Map<String, Long> stats = new HashMap<>();

        techniciens.forEach(tech -> {
            long count = interventionRepository.countByTechniciensContaining(tech);
            stats.put(tech.getNom() + " " + tech.getPrenom(), count);
        });

        return stats;
    }

    // 2. Taux de satisfaction moyen par technicien
    public Map<String, Double> averageSatisfactionByTechnicien() {
        List<Technicien> techniciens = technicienRepository.findAll();
        Map<String, Double> stats = new HashMap<>();

        techniciens.forEach(tech -> {
            Double avg = rapportRepository.findAverageSatisfactionByTechnicien(tech);
            stats.put(tech.getNom() + " " + tech.getPrenom(), avg != null ? avg : 0.0);
        });

        return stats;
    }

    // 3. Disponibilité des techniciens (pourcentage de jours disponibles)
    public Map<String, Double> calculateAvailabilityRate() {
        List<Technicien> techniciens = technicienRepository.findAll();
        Map<String, Double> stats = new HashMap<>();

        techniciens.forEach(tech -> {
            long totalDays = disponibiliteRepository.countByTechnicien(tech);
            long availableDays = disponibiliteRepository.countByTechnicienAndDisponible(tech, true);

            double rate = totalDays > 0 ? (availableDays * 100.0) / totalDays : 0.0;
            stats.put(tech.getNom() + " " + tech.getPrenom(), rate);
        });

        return stats;
    }

    // 4. Spécialité la plus sollicitée
    public Map<Specialite, Long> countInterventionsBySpecialite() {
        List<Intervention> interventions = interventionRepository.findAll();
        return interventions.stream()
                .flatMap(intervention -> intervention.getTechniciens().stream())
                .collect(Collectors.groupingBy(
                        Technicien::getSpecialite,
                        Collectors.counting()
                ));
    }

    // Version alternative avec DTO pour une meilleure structure des données
    public List<TechnicienStatistiqueDTO> getTechniciensStats() {
        return technicienRepository.findAll().stream()
                .map(tech -> {
                    TechnicienStatistiqueDTO dto = new TechnicienStatistiqueDTO();
                    dto.setTechnicienId(tech.getIdUser());
                    dto.setNomComplet(tech.getNom() + " " + tech.getPrenom());
                    dto.setNombreInterventions(interventionRepository.countByTechniciensContaining(tech));
                    dto.setSatisfactionMoyenne(rapportRepository.findAverageSatisfactionByTechnicien(tech));

                    long totalDays = disponibiliteRepository.countByTechnicien(tech);
                    long availableDays = disponibiliteRepository.countByTechnicienAndDisponible(tech, true);
                    dto.setTauxDisponibilite(totalDays > 0 ? (availableDays * 100.0) / totalDays : 0.0);

                    return dto;
                })
                .collect(Collectors.toList());
    }
}
