package com.example.cckback.controller;

import com.example.cckback.Entity.Notification;
import com.example.cckback.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationService notificationService;

    /**
     * Send a notification to a specific user via WebSocket
     * @param userId The ID of the user
     * @param notification The notification to send
     */
    public void sendNotification(Long userId, Notification notification) {
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
    }

    /**
     * Handle client request to mark a notification as read
     * @param notificationId The ID of the notification
     */
    @MessageMapping("/notifications/read/{notificationId}")
    public void markNotificationAsRead(@DestinationVariable Long notificationId) {
        // Mark the notification as read
        notificationService.markAsRead(notificationId);

        // Notify the client of the updated notification
        Notification updatedNotification = notificationService.getNotificationById(notificationId);
        messagingTemplate.convertAndSend("/topic/notifications/" + updatedNotification.getUtilisateur().getIdUser(), updatedNotification);
    }
}