package com.example.cckback.Repository;

import com.example.cckback.Entity.Alerte;
import com.example.cckback.Entity.Capteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Long> {
    @Query("SELECT a FROM Alerte a WHERE a.typePanne = :typePanne AND a.capteur.type = :typeCapteur AND a.niveauGravite = :niveauGravite")
    List<Alerte> findByTypePanneAndTypeCapteurAndNiveauGravite(
            @Param("typePanne") Alerte.TypePanne typePanne,
            @Param("typeCapteur") Capteur.TypeCapteur typeCapteur,
            @Param("niveauGravite") Alerte.NiveauGravite niveauGravite
    );
    boolean existsByCapteurAndNiveauGraviteAndEstResoluFalse(Capteur capteur, Alerte.NiveauGravite niveau);
    boolean existsByCapteurAndEstResoluFalse(Capteur capteur);
    // Pour resoudreAlertesPourCapteur
    List<Alerte> findByCapteurAndEstResoluFalse(Capteur capteur);

    List<Alerte> findByEstResolu(boolean estResolu);
    long countByCapteur(Capteur capteur);
    // Pour existeAlerteNonResoluePourCapteur
    @Query("SELECT COUNT(a), a.capteur FROM Alerte a GROUP BY a.capteur ORDER BY COUNT(a) DESC")
    List<Object[]> countAlertesGroupByCapteur();
    // Compter les alertes résolues
    long countByEstResolu(boolean estResolu);
    long countByCapteurAndDateAlerteBetween(Capteur capteur, LocalDateTime start, LocalDateTime end);
    List<Alerte> findByDateAlerteBetween(LocalDateTime start, LocalDateTime end);
    @Query("SELECT COUNT(a) FROM Alerte a WHERE a.dateAlerte BETWEEN :debut AND :fin")
    long countByDateAlerteBetween(LocalDateTime debut, LocalDateTime fin);

    List<Alerte> findByEstResoluTrue();
    @Query("SELECT a FROM Alerte a JOIN a.interventions i WHERE a.estResolu = true AND i.resolvedByAI = true AND a.dateAlerte BETWEEN :startDate AND :endDate")
    List<Alerte> findByEstResoluTrueAndInterventionsResolvedByAITrueAndDateAlerteBetween(LocalDateTime startDate, LocalDateTime endDate);
    @Query("SELECT a FROM Alerte a JOIN a.interventions i WHERE a.estResolu = true AND i.resolvedByAI = true")
    List<Alerte> findByEstResoluTrueAndInterventionsResolvedByAITrue();}
