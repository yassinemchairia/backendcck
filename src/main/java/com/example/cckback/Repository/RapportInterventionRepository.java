package com.example.cckback.Repository;
import com.example.cckback.Entity.RapportIntervention;
import com.example.cckback.Entity.Technicien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RapportInterventionRepository extends JpaRepository<RapportIntervention, Long>{
    @Query("SELECT AVG(r.satisfaction) FROM RapportIntervention r " +
            "JOIN r.intervention i " +
            "JOIN i.techniciens t " +
            "WHERE t = :technicien")
    Double findAverageSatisfactionByTechnicien(@Param("technicien") Technicien technicien);
    @Query("SELECT YEAR(i.dateDebut), MONTH(i.dateDebut), SUM(r.coutIntervention) " +
            "FROM RapportIntervention r JOIN r.intervention i " +
            "GROUP BY YEAR(i.dateDebut), MONTH(i.dateDebut) " +
            "ORDER BY YEAR(i.dateDebut) DESC, MONTH(i.dateDebut) DESC")
    List<Object[]> findMonthlyCosts();

}
