package com.example.cckback.Repository;

import com.example.cckback.Entity.CalendrierDisponibilite;
import com.example.cckback.Entity.Technicien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface CalendrierDisponibiliteRepository extends JpaRepository<CalendrierDisponibilite, Long> {
    List<CalendrierDisponibilite> findByTechnicien(Technicien technicien);
    List<CalendrierDisponibilite> findByTechnicienAndDate(Technicien technicien, LocalDate date);
    List<CalendrierDisponibilite> findByDate(LocalDate date); // Nouvelle méthode pour récupérer les disponibilités par date
    // Recherche des disponibilités d'un technicien entre deux dates
    long countByTechnicien(Technicien technicien);
    long countByTechnicienAndDisponible(Technicien technicien, boolean disponible);
    List<CalendrierDisponibilite> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<CalendrierDisponibilite> findByTechnicienAndDateAndDisponibleTrue(Technicien technicien, LocalDate date);
    List<CalendrierDisponibilite> findByDateAndDisponibleTrue(LocalDate date);

    List<CalendrierDisponibilite> findByTechnicienAndDateBetweenAndDisponibleFalse(Technicien technicien, LocalDate start, LocalDate end);
}
