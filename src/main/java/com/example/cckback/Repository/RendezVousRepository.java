package com.example.cckback.Repository;
import com.example.cckback.Entity.RendezVous;
import com.example.cckback.Entity.Technicien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    long countByTechniciensContaining(Technicien technicien);
    long countByTechniciensContainingAndNotificationEnvoyee(Technicien technicien, boolean notificationEnvoyee);
    long countByDateRendezVousBetween(LocalDateTime start, LocalDateTime end);
    @Query("SELECT r FROM RendezVous r JOIN FETCH r.techniciens WHERE r.dateRendezVous BETWEEN :start AND :end AND r.notificationEnvoyee = false")
    List<RendezVous> findByDateRendezVousBetweenAndNotificationEnvoyeeFalse(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    @Query("SELECT r FROM RendezVous r JOIN FETCH r.techniciens t LEFT JOIN FETCH r.administrateur a " +
            "WHERE t.idUser = :idUser OR a.idUser = :idUser")
    List<RendezVous> findByUserId(@Param("idUser") Long idUser);
}
