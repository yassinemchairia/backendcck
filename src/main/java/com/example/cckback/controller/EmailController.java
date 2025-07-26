package com.example.cckback.controller;

import com.example.cckback.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendTestEmail")
    public String sendTestEmail() {
        try {
            emailService.sendEmail("yassine.mchairia@esprit.tn", "Test Email", "Ceci est un e-mail de test.");
            return "Email envoyé avec succès!";
        } catch (Exception e) {
            return "Échec de l'envoi de l'e-mail: " + e.getMessage();
        }
    }
}