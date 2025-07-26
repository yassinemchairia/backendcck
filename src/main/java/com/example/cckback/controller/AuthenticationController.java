package com.example.cckback.controller;

import com.example.cckback.Entity.Technicien;
import com.example.cckback.Entity.Utilisateur;
import com.example.cckback.dto.AuthenticationRequest;
import com.example.cckback.dto.AuthenticationResponse;
import com.example.cckback.dto.RegistrationRequest;
import com.example.cckback.dto.TokenResponse;
import com.example.cckback.service.AuthenticationService;
import com.example.cckback.service.RegistrationService;
import com.example.cckback.service.TechnicienService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final TechnicienService technicienService;
    private final AuthenticationService authService;
    private final RegistrationService registrationService;

    public AuthenticationController(AuthenticationService authService, RegistrationService registrationService,TechnicienService technicienService) {
        this.authService = authService;
        this.registrationService = registrationService;
        this.technicienService = technicienService;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody AuthenticationRequest request) {
        return authService.authenticate(request.getEmail(), request.getMotDePasse());
    }

    @PostMapping("/register")
    public Utilisateur register(@RequestBody RegistrationRequest request) throws MessagingException {
        System.out.println("Requête d'inscription reçue : " + request);
        return registrationService.registerUser(
                request.getNom(),
                request.getPrenom(),
                request.getEmail(),
                request.getMotDePasse(),
                request.getNumeroTelephone(),  // Ajout du numéro de téléphone
                request.getSpecialite()  // Ajout de la spécialité
        );
    }
    @PutMapping("/validerTechnicien/{idUser}")
    public Utilisateur validerTechnicien(@PathVariable Long idUser, @RequestParam boolean isApproved) throws MessagingException {
        // Appel de la méthode du service avec le paramètre isApproved pour valider ou rejeter le technicien
        return registrationService.validerTechnicien(idUser, isApproved);
    }
    @GetMapping("/techniciens/non-valides")
    public List<Technicien> getTechniciensNonValides() {
        // Récupère la liste des techniciens non validés
        return technicienService.getTechniciensNonValides();
    }
}