package com.oncf.gare_app.service;

import com.oncf.gare_app.dto.NotificationResponse;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.TypeNotificationEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    void createNotification(UtilisateurSysteme destinataire, TypeNotificationEnum type,
                            String message, String lien);

    void createNotificationForDocument(String documentType, Long documentId,
                                       String action, UtilisateurSysteme userToNotify);

    List<NotificationResponse> getNotificationsForUser(Long userId);

    List<NotificationResponse> getUnreadNotificationsForUser(Long userId);

    NotificationResponse markNotificationAsRead(Long notificationId);

    void markAllNotificationsAsRead(Long userId);

    long countUnreadNotifications(Long userId);

    Page<NotificationResponse> searchNotifications(
            Long destinataireId,
            TypeNotificationEnum type,
            Boolean lue,
            Pageable pageable);
}