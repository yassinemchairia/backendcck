package com.example.cckback.Repository;
import com.example.cckback.Entity.HistoriqueIntervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueInterventionRepository extends JpaRepository<HistoriqueIntervention, Long> {
    List<HistoriqueIntervention> findByIntervention_IdInterv(Long idInterv);

    @Query("SELECT h FROM HistoriqueIntervention h WHERE LOWER(h.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(h.rapport) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<HistoriqueIntervention> findByDescriptionOrRapportContaining(String searchTerm);
}
