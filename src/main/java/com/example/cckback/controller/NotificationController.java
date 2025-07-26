package com.example.cckback.controller;

import com.example.cckback.Entity.Notification;
import com.example.cckback.Entity.Utilisateur;
import com.example.cckback.service.NotificationService;
import com.example.cckback.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository utilisateurRepository;

    /**
     * Get all unread notifications for a user
     * @param idUser The ID of the user
     * @return List of unread notifications
     */
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@RequestParam Long idUser) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + idUser));
        List<Notification> notifications = notificationService.getUnreadNotifications(utilisateur);
        return ResponseEntity.ok(notifications);
    }
    /**
     * Get the count of unread notifications for a user
     * @param idUser The ID of the user
     * @return Number of unread notifications
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadNotificationsCount(@RequestParam Long idUser) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + idUser));
        long count = notificationService.countUnreadNotifications(utilisateur);
        return ResponseEntity.ok(count);
    }
    /**
     * Get all notifications for a user
     * @param idUser The ID of the user
     * @return List of all notifications
     */
    @GetMapping("/all")
    public ResponseEntity<List<Notification>> getAllNotifications(@RequestParam Long idUser) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + idUser));
        List<Notification> notifications = notificationService.getAllNotifications(utilisateur);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get notifications by type for a user
     * @param idUser The ID of the user
     * @param type The notification type
     * @return List of notifications of the specified type
     */
    @GetMapping("/type")
    public ResponseEntity<List<Notification>> getNotificationsByType(@RequestParam Long idUser,
                                                                     @RequestParam Notification.NotificationType type) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + idUser));
        List<Notification> notifications = notificationService.getNotificationsByType(utilisateur, type);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Mark a single notification as read
     * @param idNotification The ID of the notification
     * @return Success message
     */
    @PostMapping("/read/{idNotification}")
    public ResponseEntity<String> markAsRead(@PathVariable Long idNotification) {
        notificationService.markAsRead(idNotification);
        return ResponseEntity.ok("Notification marked as read");
    }

    /**
     * Mark multiple notifications as read
     * @param notificationIds List of notification IDs
     * @return Success message
     */
    @PostMapping("/read/multiple")
    public ResponseEntity<String> markMultipleAsRead(@RequestBody List<Long> notificationIds) {
        notificationService.markMultipleAsRead(notificationIds);
        return ResponseEntity.ok("Notifications marked as read");
    }
}