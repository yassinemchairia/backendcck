package com.example.cckback.Repository;
import com.example.cckback.Entity.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {

    Optional<Administrateur> findByIdUser(Long aLong);
}
