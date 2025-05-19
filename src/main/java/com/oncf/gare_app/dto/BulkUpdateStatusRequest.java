package com.oncf.gare_app.dto;

import com.oncf.gare_app.enums.StatutEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkUpdateStatusRequest {
    private List<Long> ids;
    private StatutEnum newStatus;
    private String commentaire;
}