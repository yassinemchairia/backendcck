package com.example.cckback.service;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AdministrateurRepository;
import com.example.cckback.Repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationService {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private AdministrateurRepository administrateurRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService; // Injectez EmailService

    public RegistrationService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public Utilisateur registerUser(String nom, String prenom, String email, String motDePasse, String numeroTelephone, Specialite specialite) throws MessagingException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà.");
        }

        // Hash le mot de passe
        String encodedPassword = passwordEncoder.encode(motDePasse);

        // Crée l'utilisateur en tant que technicien par défaut
        Technicien technicien = new Technicien();
        technicien.setSpecialite(specialite);  // Ajoute la spécialité
        technicien.setNumeroTelephone(numeroTelephone);  // Ajoute le numéro de téléphone
        technicien.setValide(false);  // L'utilisateur est non validé par défaut

        technicien.setNom(nom);
        technicien.setPrenom(prenom);
        technicien.setEmail(email);
        technicien.setMotDePasse(encodedPassword);
        technicien.setRole(Role.TECHNICIEN);  // Le rôle est fixé à Technicien

        // Enregistrer le technicien dans la base de données
        Utilisateur savedUser = userRepository.save(technicien);

        // Envoyer les e-mails après l'enregistrement de l'utilisateur
        sendTechnicianEmails(technicien, email);
        notifyAdminsOfNewTechnician((Technicien) savedUser);

        return savedUser;
    }
    private void notifyAdminsOfNewTechnician(Technicien technicien) {
        List<Administrateur> admins = administrateurRepository.findAll();
        for (Administrateur admin : admins) {
            String message = String.format("Un nouveau technicien %s %s (%s) a demandé à s'inscrire. Spécialité: %s, Téléphone: %s",
                    technicien.getPrenom(),
                    technicien.getNom(),
                    technicien.getEmail(),
                    technicien.getSpecialite(),
                    technicien.getNumeroTelephone());

            notificationService.createNotification(
                    admin,
                    Notification.NotificationType.TECHNICIAN_REGISTRATION,
                    message,
                    null,  // Pas d'intervention associée
                    null,  // Pas de rendez-vous associé
                    null  // Pas d'alerte associée
            );
        }
    }
    private void sendTechnicianEmails(Technicien technicien, String email) throws MessagingException {
        // E-mail à l'utilisateur
        String userSubject = "Votre inscription a été reçue";
        String userBody = "Bonjour " + technicien.getNom() + ",\n\nVotre inscription en tant que technicien a bien été reçue. Vous devez attendre la confirmation de l'administrateur pour activer votre compte.";
        emailService.sendEmail(email, userSubject, userBody);

        // E-mail à l'administrateur
        Utilisateur admin = userRepository.findByRole(Role.ADMIN)
                .orElseThrow(() -> new RuntimeException("Aucun administrateur trouvé"));

        // E-mail à l'administrateur
        String adminSubject = "Un nouveau technicien souhaite s'inscrire";
        String adminBody = "Bonjour Administrateur,\n\nUn technicien a soumis sa demande d'inscription.\n\nDétails :\nNom: " + technicien.getNom() +
                "\nPrénom: " + technicien.getPrenom() +
                "\nEmail: " + email + "\n\nVeuillez valider ou refuser cette inscription.";
        emailService.sendEmail(admin.getEmail(), adminSubject, adminBody); // Utilisez l'email de l'administrateur trouvé.
    }

    public Utilisateur validerTechnicien(Long idUser, boolean isApproved) throws MessagingException {
        Utilisateur utilisateur = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (utilisateur.getRole() != Role.TECHNICIEN) {
            throw new RuntimeException("Ce n'est pas un technicien");
        }

        // Mettre à jour la validation du technicien
        if (!isApproved) {
            // Suppression du technicien de la base de données
            sendRejectionEmail((Technicien) utilisateur); // Envoi de l'email de rejet au technicien
            userRepository.delete(utilisateur);
            return utilisateur; // Retourne l'utilisateur supprimé
        }

        // Si isApproved est true, on valide le technicien
        utilisateur.setValide(true);

        // Enregistrer les changements dans la base de données
        Utilisateur updatedUser = userRepository.save(utilisateur);

        // Envoi de l'email au technicien pour confirmer la validation
        sendValidationEmail((Technicien) updatedUser, true);

        return updatedUser;
    }
    private void sendRejectionEmail(Technicien technicien) throws MessagingException {
        String subject = "Votre inscription a été rejetée";
        String body = "Bonjour " + technicien.getNom() + ",\n\n" +
                "Désolé, votre inscription en tant que technicien a été rejetée. Si vous avez des questions, contactez l'administrateur.";

        emailService.sendEmail(technicien.getEmail(), subject, body);
    }
    // Méthode pour envoyer l'e-mail au technicien
    private void sendValidationEmail(Technicien technicien, boolean isApproved) throws MessagingException {
        // Sujet de l'e-mail
        String subject = isApproved ? "Votre inscription a été validée" : "Votre inscription a été rejetée";

        // Corps de l'e-mail
        String body = "Bonjour " + technicien.getNom() + ",\n\n";
        if (isApproved) {
            body += "Félicitations ! Votre inscription en tant que technicien a été validée. Vous pouvez désormais accéder à vos fonctionnalités.";
        }

        // Envoi de l'e-mail au technicien
        emailService.sendEmail(technicien.getEmail(), subject, body);
    }

}