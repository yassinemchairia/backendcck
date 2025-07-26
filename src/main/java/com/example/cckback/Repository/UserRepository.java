package com.example.cckback.Repository;

import com.example.cckback.Entity.Role;
import com.example.cckback.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByRole(Role role);


    Optional<Utilisateur> findByIdUser(Long aLong);
    boolean existsByRole(Role role);
    long countByRole(Role role);
    Optional<Utilisateur> findByEmailAndResetPasswordToken(String email, String token);

    List<Utilisateur> findByRoleAndValide(Role role, boolean valide);
}
