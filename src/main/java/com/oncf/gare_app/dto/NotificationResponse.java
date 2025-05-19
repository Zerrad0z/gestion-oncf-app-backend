package com.oncf.gare_app.dto;

import com.oncf.gare_app.enums.TypeNotificationEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private UtilisateurResponse destinataire;
    private TypeNotificationEnum type;
    private String message;
    private boolean lue;
    private LocalDateTime dateCreation;
    private LocalDateTime dateLecture;
    private String lien;
    private String formattedDate;
    private String timeAgo;
}