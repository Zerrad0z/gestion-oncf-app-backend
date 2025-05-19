package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.dto.NotificationResponse;
import com.oncf.gare_app.entity.Notification;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.TypeNotificationEnum;
import com.oncf.gare_app.exception.ResourceNotFoundException;
import com.oncf.gare_app.mapper.NotificationMapper;
import com.oncf.gare_app.repository.NotificationRepository;
import com.oncf.gare_app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    @Override
    public void createNotification(UtilisateurSysteme destinataire, TypeNotificationEnum type,
                                   String message, String lien) {
        Notification notification = Notification.builder()
                .destinataire(destinataire)
                .type(type)
                .message(message)
                .lue(false)
                .lien(lien)
                .build();

        notification = notificationRepository.save(notification);

        // Send real-time notification via WebSocket
        NotificationResponse notificationResponse = notificationMapper.toDto(notification);
        messagingTemplate.convertAndSendToUser(
                destinataire.getNomUtilisateur(),
                "/queue/notifications",
                notificationResponse
        );
    }

    @Transactional
    @Override
    public void createNotificationForDocument(String documentType, Long documentId,
                                              String action, UtilisateurSysteme userToNotify) {
        String documentInfo = documentType + " #" + documentId;
        String message = "Le document " + documentInfo + " a été " + action;
        String lien = "/documents/" + documentType.toLowerCase() + "/" + documentId;

        TypeNotificationEnum type = TypeNotificationEnum.INFO;
        if (action.contains("urgent") || action.contains("priorité")) {
            type = TypeNotificationEnum.URGENT;
        } else if (action.contains("attente") || action.contains("assigné")) {
            type = TypeNotificationEnum.ALERTE;
        }

        createNotification(userToNotify, type, message, lien);
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationResponse> getNotificationsForUser(Long userId) {
        return notificationRepository.findByDestinataire_IdOrderByDateCreationDesc(userId)
                .stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationResponse> getUnreadNotificationsForUser(Long userId) {
        return notificationRepository.findByDestinataire_IdAndLueFalseOrderByDateCreationDesc(userId)
                .stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public NotificationResponse markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification non trouvée avec l'id: " + notificationId));

        notification.setLue(true);
        notification.setDateLecture(LocalDateTime.now());

        notification = notificationRepository.save(notification);

        return notificationMapper.toDto(notification);
    }

    @Transactional
    @Override
    public void markAllNotificationsAsRead(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByDestinataire_IdAndLueFalseOrderByDateCreationDesc(userId);

        for (Notification notification : notifications) {
            notification.setLue(true);
            notification.setDateLecture(LocalDateTime.now());
        }

        notificationRepository.saveAll(notifications);
    }

    @Transactional(readOnly = true)
    @Override
    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countByDestinataire_IdAndLueFalse(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<NotificationResponse> searchNotifications(
            Long destinataireId, TypeNotificationEnum type, Boolean lue, Pageable pageable) {

        return notificationRepository.search(destinataireId, type, lue, pageable)
                .map(notificationMapper::toDto);
    }
}