package com.example.cckback.Repository;


import com.example.cckback.Entity.Intervention;
import com.example.cckback.Entity.Statut;
import com.example.cckback.Entity.Technicien;
import com.example.cckback.Entity.TypeIntervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Long> {

    @Query("SELECT i FROM Intervention i LEFT JOIN FETCH i.techniciens LEFT JOIN FETCH i.alerte")
    List<Intervention> findAllWithTechniciensAndAlerte();
    List<Intervention> findByStatut(Statut statut);
    long countByTypeIntervention(TypeIntervention type);

    List<Intervention> findByDateDebutBetween(LocalDateTime start, LocalDateTime end);
    long countByTechniciensContaining(Technicien technicien);
    @Query("SELECT i FROM Intervention i LEFT JOIN FETCH i.rapport")
    List<Intervention> findAllWithRapport();

    @Query("SELECT i.typeIntervention, AVG(r.coutIntervention) " +
            "FROM Intervention i JOIN i.rapport r " +
            "GROUP BY i.typeIntervention")
    List<Object[]> findAverageCostByType();
    @Query("SELECT DISTINCT i FROM Intervention i " +
            "LEFT JOIN FETCH i.rapport " +
            "LEFT JOIN FETCH i.techniciens")
    List<Intervention> findAllWithRapportAndTechniciens();
    @Query("SELECT AVG(r.satisfaction) FROM RapportIntervention r WHERE r.satisfaction > 0")
    Double findGlobalAverageSatisfaction();
    @Query("SELECT YEAR(i.dateDebut), MONTH(i.dateDebut), AVG(r.satisfaction) " +
            "FROM RapportIntervention r JOIN r.intervention i " +
            "WHERE r.satisfaction > 0 " +
            "GROUP BY YEAR(i.dateDebut), MONTH(i.dateDebut) " +
            "ORDER BY YEAR(i.dateDebut), MONTH(i.dateDebut)")
    List<Object[]> findMonthlyAverageSatisfaction();

    List<Intervention> findByTypeIntervention(TypeIntervention type);


    @Query("SELECT i FROM Intervention i WHERE i.typeIntervention = :type AND i.dateDebut BETWEEN :debut AND :fin")
    List<Intervention> findByTypeAndDateBetween(TypeIntervention type, LocalDateTime debut, LocalDateTime fin);
    @Query("SELECT i FROM Intervention i JOIN FETCH i.techniciens WHERE i.statut = :statut AND i.dateDebut < :date")
    List<Intervention> findByStatutAndDateDebutBefore(@Param("statut") Statut statut, @Param("date") LocalDateTime date);

    @Query("SELECT i FROM Intervention i JOIN FETCH i.alerte a JOIN FETCH a.capteur WHERE i.idInterv = :id")
    Optional<Intervention> findById(@Param("id") Long id);
    List<Intervention> findByTechniciens_IdUser(Long idUser);

}

