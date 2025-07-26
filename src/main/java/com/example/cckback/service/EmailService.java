package com.example.cckback.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String subject, String body) throws MailException, MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // true pour envoyer en HTML

        javaMailSender.send(mimeMessage);
    }

    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        String subject = "Réinitialisation de votre mot de passe";
        String resetLink = "http://localhost:4200/auth/password-reset?token=" + token + "&email=" + to;
        String body = "<p>Bonjour,</p>"
                + "<p>Vous avez demandé à réinitialiser votre mot de passe.</p>"
                + "<p>Cliquez sur le lien ci-dessous pour changer votre mot de passe :</p>"
                + "<p><a href=\"" + resetLink + "\">Changer mon mot de passe</a></p>"
                + "<p>Ce lien expirera dans 24 heures.</p>"
                + "<p>Si vous n'avez pas demandé cette réinitialisation, ignorez simplement cet email.</p>";

        sendEmail(to, subject, body);
    }
}