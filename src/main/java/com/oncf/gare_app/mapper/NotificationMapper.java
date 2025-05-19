package com.oncf.gare_app.mapper;

import com.oncf.gare_app.dto.NotificationResponse;
import com.oncf.gare_app.entity.Notification;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", uses = {UtilisateurMapper.class})
public abstract class NotificationMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Mapping(target = "formattedDate", ignore = true)
    @Mapping(target = "timeAgo", ignore = true)
    public abstract NotificationResponse toDto(Notification entity);

    @AfterMapping
    protected void setAdditionalFields(@MappingTarget NotificationResponse response, Notification entity) {
        // Format date
        if (entity.getDateCreation() != null) {
            response.setFormattedDate(entity.getDateCreation().format(DATE_FORMATTER));
            response.setTimeAgo(calculateTimeAgo(entity.getDateCreation()));
        }
    }

    private String calculateTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "À l'instant";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + " heure" + (hours > 1 ? "s" : "") + " ago";
        } else if (seconds < 604800) {
            long days = seconds / 86400;
            return days + " jour" + (days > 1 ? "s" : "") + " ago";
        } else if (seconds < 2592000) {
            long weeks = seconds / 604800;
            return weeks + " semaine" + (weeks > 1 ? "s" : "") + " ago";
        } else if (seconds < 31536000) {
            long months = seconds / 2592000;
            return months + " mois" + " ago";
        } else {
            long years = seconds / 31536000;
            return years + " an" + (years > 1 ? "s" : "") + " ago";
        }
    }
}