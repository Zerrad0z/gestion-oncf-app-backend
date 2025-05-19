package com.oncf.gare_app.dto;

import com.oncf.gare_app.enums.TypeDocumentEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueTraitementResponse {
    private Long id;
    private TypeDocumentEnum typeDocument;
    private Long documentId;
    private UtilisateurResponse utilisateur;
    private LocalDateTime dateAction;
    private String action;
    private String details;
    private String ancienStatut;
    private String nouveauStatut;
    private String documentTitle;
    private String formattedDate;
}