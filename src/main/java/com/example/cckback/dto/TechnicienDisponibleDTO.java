package com.example.cckback.dto;

import com.example.cckback.Entity.Specialite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicienDisponibleDTO {
    private Long idUser;
    private String nom;
    private String prenom;
    private String email;
    private Specialite specialite;
    private String numeroTelephone;
    private boolean valide;
    private LocalDate dateDisponibilite;
}