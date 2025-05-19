package com.oncf.gare_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCountResponse {
    private long total;
    private long unread;
    private long info;
    private long alerte;
    private long urgent;
}