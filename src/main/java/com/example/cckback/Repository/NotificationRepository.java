package com.example.cckback.Repository;

import com.example.cckback.Entity.Notification;
import com.example.cckback.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUtilisateurAndIsReadFalseOrderByCreatedAtDesc(Utilisateur utilisateur);
    long countByUtilisateurAndIsReadFalse(Utilisateur utilisateur);

    // Retrieve all notifications for a user, sorted by creation date (newest first)
    List<Notification> findByUtilisateurOrderByCreatedAtDesc(Utilisateur utilisateur);

    // Retrieve notifications by type for a user
    List<Notification> findByUtilisateurAndTypeOrderByCreatedAtDesc(Utilisateur utilisateur, Notification.NotificationType type);
}