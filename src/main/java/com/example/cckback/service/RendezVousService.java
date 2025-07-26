package com.example.cckback.service;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.*;
import com.example.cckback.dto.AutoPlanificationRequest;
import com.example.cckback.dto.RendezVousRequest;
import com.example.cckback.dto.RendezVousStatsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RendezVousService {
@Autowired
private NotificationService notificationService;

    @Autowired
    private RendezVousRepository rendezVousRepository;
    @Autowired
    private TechnicienRepository technicienRepository;
    @Autowired
    private AdministrateurRepository administrateurRepository;
    @Autowired
    private CalendrierDisponibiliteRepository calendrierDisponibiliteRepository;
@Autowired
private UserRepository userRepository;
    /**
     * Suggest appointment planning based on technician availability and specialization
     */
    public Map<String, Object> suggererPlanification(AutoPlanificationRequest request) {
        // 1. Récupérer les techniciens avec la spécialité requise
        List<Technicien> techniciensEligibles = technicienRepository.findBySpecialite(request.getSpecialiteRequise());
        if (techniciensEligibles.isEmpty()) {
            throw new RuntimeException("Aucun technicien disponible avec la spécialité requise : " + request.getSpecialiteRequise());
        }

        // 2. Définir la plage de recherche
        LocalDate startDate = request.getDateSouhaitee();
        LocalDate endDate = request.getDateLimite() != null ? request.getDateLimite() : startDate.plusDays(7);

        // 3. Trouver les créneaux disponibles
        List<RendezVous> suggestions = new ArrayList<>();
        int maxTechniciansAvailable = 0;
        for (LocalDate currentDate = startDate; !currentDate.isAfter(endDate); currentDate = currentDate.plusDays(1)) {
            // Vérifier les techniciens disponibles pour ce jour
            LocalDate finalCurrentDate = currentDate;
            List<Technicien> techniciensDisponibles = techniciensEligibles.stream()
                    .filter(tech -> estTechnicienDisponible(tech, finalCurrentDate))
                    .collect(Collectors.toList());

            maxTechniciansAvailable = Math.max(maxTechniciansAvailable, techniciensDisponibles.size());

            if (!techniciensDisponibles.isEmpty()) {
                // Créer une suggestion de rendez-vous avec les techniciens disponibles
                RendezVous rdv = new RendezVous();
                rdv.setDescription(request.getDescription());
                rdv.setDateRendezVous(currentDate.atStartOfDay());
                rdv.setNotificationEnvoyee(false);
                rdv.setAdministrateur(administrateurRepository.findById(request.getAdminId())
                        .orElseThrow(() -> new RuntimeException("Administrateur non trouvé")));
                rdv.setTechniciens(techniciensDisponibles.stream()
                        .limit(request.getNombreTechniciensRequis())
                        .collect(Collectors.toList()));
                suggestions.add(rdv);

                if (suggestions.size() >= 3) { // Limiter à 3 suggestions
                    break;
                }
            }
        }

        // Prepare response with suggestions and warning if necessary
        Map<String, Object> response = new HashMap<>();
        response.put("suggestions", suggestions);
        if (maxTechniciansAvailable < request.getNombreTechniciensRequis()) {
            response.put("warning", "Seulement " + maxTechniciansAvailable + " technicien(s) disponible(s) pour la période et la spécialité demandées.");
        }
        if (suggestions.isEmpty()) {
            throw new RuntimeException("Aucun créneau disponible pour la période spécifiée avec la spécialité requise.");
        }

        return response;
    }

    /**
     * Check if a technician is available on a given date
     */
    private boolean estTechnicienDisponible(Technicien technicien, LocalDate date) {
        // Vérifier la disponibilité dans CalendrierDisponibilite
        List<CalendrierDisponibilite> dispos = calendrierDisponibiliteRepository
                .findByTechnicienAndDateAndDisponibleTrue(technicien, date);
        if (dispos.isEmpty()) {
            return false;
        }

        // Vérifier qu'il n'y a pas de rendez-vous conflictuels pour ce jour
        long conflits = rendezVousRepository.findAll().stream()
                .filter(rdv -> rdv.getTechniciens().contains(technicien))
                .filter(rdv -> rdv.getDateRendezVous().toLocalDate().equals(date))
                .count();

        return conflits == 0;
    }

    /**
     * Add a new appointment and notify participants
     */
    public RendezVous ajouterRendezVous(Long adminId, String description, LocalDateTime date, List<Long> technicienIds) {
        // Fetch technicians by IDs
        List<Technicien> techniciens = technicienRepository.findAllById(technicienIds);
        if (techniciens.isEmpty()) {
            throw new RuntimeException("Aucun technicien valide sélectionné");
        }

        // Create the RendezVous
        RendezVous rendezVous = new RendezVous();
        rendezVous.setDescription(description);
        rendezVous.setDateRendezVous(date);
        rendezVous.setNotificationEnvoyee(false);

        // Fetch and set the admin
        rendezVous.setAdministrateur(administrateurRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé")));

        // Set technicians
        rendezVous.setTechniciens(techniciens);

        // Save the rendez-vous
        RendezVous savedRendezVous = rendezVousRepository.save(rendezVous);

        // Generate APPOINTMENT_ASSIGNED notifications for each technician
        for (Technicien technicien : techniciens) {
            String message = String.format("Vous avez été assigné à un rendez-vous le %s: %s.",
                    savedRendezVous.getDateRendezVous(), savedRendezVous.getDescription());
            notificationService.createNotification(
                    technicien,
                    Notification.NotificationType.APPOINTMENT_ASSIGNED,
                    message,
                    null,
                    savedRendezVous,
                    null
            );
        }

        // Notify the admin
        String adminMessage = String.format("Vous avez créé un rendez-vous le %s: %s.",
                savedRendezVous.getDateRendezVous(), savedRendezVous.getDescription());
        notificationService.createNotification(
                savedRendezVous.getAdministrateur(),
                Notification.NotificationType.APPOINTMENT_ASSIGNED,
                adminMessage,
                null,
                savedRendezVous,
                null
        );

        return savedRendezVous;
    }

    /**
     * Get all appointments for a given user ID (Technicien or Administrateur)
     * @param idUser The ID of the user
     * @return List of RendezVous
     */
    public List<RendezVous> getRendezVousByUserId(Long idUser) {
        return rendezVousRepository.findByUserId(idUser);
    }

    /**
     * Count appointments by month
     */
    public Map<String, Long> countRendezVousByMonth(int monthsBack) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(monthsBack - 1).withDayOfMonth(1);

        Map<YearMonth, Long> monthlyCounts = rendezVousRepository.findAll()
                .stream()
                .filter(rdv -> rdv.getDateRendezVous() != null)
                .filter(rdv -> {
                    LocalDate rdvDate = rdv.getDateRendezVous().toLocalDate();
                    return !rdvDate.isBefore(startDate) && !rdvDate.isAfter(endDate);
                })
                .collect(Collectors.groupingBy(
                        rdv -> YearMonth.from(rdv.getDateRendezVous()),
                        Collectors.counting()
                ));

        Map<String, Long> result = new TreeMap<>();
        for (int i = 0; i < monthsBack; i++) {
            YearMonth month = YearMonth.from(startDate.plusMonths(i));
            String monthName = month.getMonth().toString() + " " + month.getYear();
            result.put(monthName, monthlyCounts.getOrDefault(month, 0L));
        }

        return result;
    }

    /**
     * Count appointments by week
     */
    public Map<String, Long> countRendezVousByWeek(int weeksBack) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(weeksBack - 1)
                .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        Map<String, Long> weeklyCounts = new TreeMap<>(Collections.reverseOrder());

        for (int i = 0; i < weeksBack; i++) {
            LocalDate weekStart = startDate.plusWeeks(i);
            LocalDate weekEnd = weekStart.plusDays(6);
            String weekKey = "S" + weekStart.get(java.time.temporal.WeekFields.ISO.weekOfYear()) +
                    " (" + weekStart + " - " + weekEnd + ")";

            long count = rendezVousRepository.countByDateRendezVousBetween(
                    weekStart.atStartOfDay(),
                    weekEnd.atTime(23, 59, 59));

            weeklyCounts.put(weekKey, count);
        }

        return weeklyCounts;
    }

    /**
     * Calculate participation rate for technicians
     */
    public Map<String, Double> calculateParticipationRate() {
        List<Technicien> techniciens = technicienRepository.findAll();
        Map<String, Double> participationRates = new HashMap<>();

        techniciens.forEach(tech -> {
            long totalRdvs = rendezVousRepository.countByTechniciensContaining(tech);
            long attendedRdvs = rendezVousRepository.countByTechniciensContainingAndNotificationEnvoyee(tech, true);

            double rate = totalRdvs > 0 ? (attendedRdvs * 100.0) / totalRdvs : 0.0;
            participationRates.put(tech.getNom() + " " + tech.getPrenom(), rate);
        });

        return participationRates;
    }

    /**
     * Get appointment statistics for technicians
     */
    public List<RendezVousStatsDTO> getRendezVousStats() {
        return technicienRepository.findAll().stream()
                .map(tech -> {
                    RendezVousStatsDTO dto = new RendezVousStatsDTO();
                    dto.setTechnicienId(tech.getIdUser());
                    dto.setNomComplet(tech.getNom() + " " + tech.getPrenom());

                    long totalRdvs = rendezVousRepository.countByTechniciensContaining(tech);
                    long attendedRdvs = rendezVousRepository.countByTechniciensContainingAndNotificationEnvoyee(tech, true);

                    dto.setNombreRdvsTotal(totalRdvs);
                    dto.setNombreRdvsPresents(attendedRdvs);
                    dto.setTauxParticipation(totalRdvs > 0 ? (attendedRdvs * 100.0) / totalRdvs : 0.0);

                    return dto;
                })
                .sorted(Comparator.comparingDouble(RendezVousStatsDTO::getTauxParticipation).reversed())
                .collect(Collectors.toList());
    }
}