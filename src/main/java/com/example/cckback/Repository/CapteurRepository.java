package com.example.cckback.Repository;

import com.example.cckback.Entity.Capteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CapteurRepository extends JpaRepository<Capteur, Long> {
    Optional<Capteur> findByIpAdresse(String ipAdresse);
    List<Capteur> findByDepartement(Capteur.Departement departement);
    List<Capteur> findByType(Capteur.TypeCapteur type);
    @Query("SELECT c FROM Capteur c LEFT JOIN FETCH c.alertes a WHERE a.estResolu = false")
    List<Capteur> findAllWithAlertes();}
