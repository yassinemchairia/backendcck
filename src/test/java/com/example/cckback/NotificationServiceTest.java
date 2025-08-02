package com.example.cckback;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.NotificationRepository;
import com.example.cckback.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;
    private Technicien technicien;

    @BeforeEach
    void setUp() {
        technicien = new Technicien();
        technicien.setIdUser(1L);
        technicien.setEmail("tech@example.com");

        notification = new Notification();
        notification.setIdNotification(1L);
        notification.setUtilisateur(technicien);
        notification.setType(Notification.NotificationType.INTERVENTION_ASSIGNED);
        notification.setMessage("Test message");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createNotification_ShouldSaveAndSendNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.createNotification(
                technicien,
                Notification.NotificationType.INTERVENTION_ASSIGNED,
                "Message",
                null,
                null,
                null);

        assertNotNull(result);
        verify(messagingTemplate).convertAndSend(eq("/topic/notifications/1"), any(Notification.class));
    }

    @Test
    void getUnreadNotifications_ShouldReturnOnlyUnread() {
        when(notificationRepository.findByUtilisateurAndIsReadFalseOrderByCreatedAtDesc(technicien))
                .thenReturn(Collections.singletonList(notification));

        List<Notification> result = notificationService.getUnreadNotifications(technicien);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isRead());
    }

    @Test
    void markAsRead_ShouldUpdateNotification() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(1L);

        assertTrue(notification.isRead());
        assertNotNull(notification.getReadAt());
        verify(notificationRepository).save(notification);
    }

    @Test
    void getNotificationById_ShouldReturnNotification() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        Notification result = notificationService.getNotificationById(1L);

        assertEquals("Test message", result.getMessage());
    }
}