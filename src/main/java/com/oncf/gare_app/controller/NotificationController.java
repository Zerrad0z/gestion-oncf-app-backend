package com.oncf.gare_app.controller;

import com.oncf.gare_app.dto.NotificationResponse;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.TypeNotificationEnum;
import com.oncf.gare_app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UtilisateurSysteme) userDetails).getId();
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getMyUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UtilisateurSysteme) userDetails).getId();
        return ResponseEntity.ok(notificationService.getUnreadNotificationsForUser(userId));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> countUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UtilisateurSysteme) userDetails).getId();
        long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markNotificationAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markNotificationAsRead(id));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UtilisateurSysteme) userDetails).getId();
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<NotificationResponse>> searchNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) TypeNotificationEnum type,
            @RequestParam(required = false) Boolean lue,
            Pageable pageable) {

        Long userId = ((UtilisateurSysteme) userDetails).getId();
        return ResponseEntity.ok(notificationService.searchNotifications(userId, type, lue, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsForUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }
}