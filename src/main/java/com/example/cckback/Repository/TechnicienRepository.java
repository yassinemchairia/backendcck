package com.example.cckback.Repository;

import com.example.cckback.Entity.Specialite;
import com.example.cckback.Entity.Technicien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TechnicienRepository extends JpaRepository<Technicien, Long> {
    List<Technicien> findBySpecialite(Specialite specialite);
    List<Technicien> findAllByIdUser(Long idUser);
    Optional <Technicien> findByIdUser(Long idUser);
    List<Technicien> findAllByIdUserIn(List<Long> idUsers); // Méthode pour récupérer les techniciens par leur ID utilisateur
    List<Technicien> findByValideFalse();
    Optional<Technicien> findByEmail(String email);

}

