package com.example.cckback.service;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.NotificationRepository;
import org.eclipse.paho.client.mqttv3.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Create a new notification and send it to the user via WebSocket
     * @param utilisateur The recipient (Technicien or Administrateur)
     * @param type The type of notification
     * @param message The notification message
     * @param intervention Optional related intervention
     * @param rendezVous Optional related appointment
     * @param alerte Optional related alert
     * @return The created notification
     */
    public Notification createNotification(Utilisateur utilisateur, Notification.NotificationType type, String message,
                                           Intervention intervention, RendezVous rendezVous, Alerte alerte) {
        Notification notification = new Notification();
        notification.setUtilisateur(utilisateur);
        notification.setType(type);
        notification.setMessage(message);
        notification.setIntervention(intervention);
        notification.setRendezVous(rendezVous);
        notification.setAlerte(alerte);
        notification.setRead(false);
        notificationRepository.save(notification);

        // Send notification to the user via WebSocket
        sendNotificationToUser(utilisateur.getIdUser(), notification);

        return notification;
    }

    /**
     * Retrieve unread notifications for a user
     * @param utilisateur The user
     * @return List of unread notifications
     */
    public List<Notification> getUnreadNotifications(Utilisateur utilisateur) {
        return notificationRepository.findByUtilisateurAndIsReadFalseOrderByCreatedAtDesc(utilisateur);
    }
    /**
     * Count unread notifications for a user
     * @param utilisateur The user
     * @return Number of unread notifications
     */
    public long countUnreadNotifications(Utilisateur utilisateur) {
        return notificationRepository.countByUtilisateurAndIsReadFalse(utilisateur);
    }

    /**
     * Retrieve all notifications for a user
     * @param utilisateur The user
     * @return List of all notifications
     */
    public List<Notification> getAllNotifications(Utilisateur utilisateur) {
        return notificationRepository.findByUtilisateurOrderByCreatedAtDesc(utilisateur);
    }

    /**
     * Retrieve notifications by type for a user
     * @param utilisateur The user
     * @param type The notification type
     * @return List of notifications of the specified type
     */
    public List<Notification> getNotificationsByType(Utilisateur utilisateur, Notification.NotificationType type) {
        return notificationRepository.findByUtilisateurAndTypeOrderByCreatedAtDesc(utilisateur, type);
    }
    /**
     * Retrieve a notification by its ID
     * @param notificationId The ID of the notification
     * @return The notification
     */
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
    }
    /**
     * Mark a notification as read
     * @param idNotification The ID of the notification
     */
    public void markAsRead(Long idNotification) {
        Notification notification = notificationRepository.findById(idNotification)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + idNotification));
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    /**
     * Mark multiple notifications as read
     * @param notificationIds List of notification IDs
     */
    public void markMultipleAsRead(List<Long> notificationIds) {
        for (Long id : notificationIds) {
            markAsRead(id);
        }
    }

    /**
     * Send a notification to a user via WebSocket
     * @param userId The ID of the user
     * @param notification The notification to send
     */
    private void sendNotificationToUser(Long userId, Notification notification) {
        System.out.println("Tentative d'envoi WebSocket à user " + userId);
        System.out.println("Message : " + notification.getMessage());
        System.out.println("Destination : /topic/notifications/" + userId);

        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);

        System.out.println("Message envoyé via messagingTemplate");
    }
}