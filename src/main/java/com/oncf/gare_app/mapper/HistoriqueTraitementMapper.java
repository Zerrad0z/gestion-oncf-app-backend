package com.oncf.gare_app.mapper;

import com.oncf.gare_app.dto.HistoriqueTraitementResponse;
import com.oncf.gare_app.entity.HistoriqueTraitement;
import com.oncf.gare_app.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class HistoriqueTraitementMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UtilisateurMapper utilisateurMapper;

    public HistoriqueTraitementResponse toDto(HistoriqueTraitement entity) {
        if (entity == null) {
            return null;
        }

        HistoriqueTraitementResponse response = new HistoriqueTraitementResponse();
        response.setId(entity.getId());
        response.setTypeDocument(entity.getTypeDocument());
        response.setDocumentId(entity.getDocumentId());
        response.setUtilisateur(entity.getUtilisateur() != null ? utilisateurMapper.toDto(entity.getUtilisateur()) : null);
        response.setDateAction(entity.getDateAction());
        response.setAction(entity.getAction());
        response.setDetails(entity.getDetails());
        response.setAncienStatut(entity.getAncienStatut());
        response.setNouveauStatut(entity.getNouveauStatut());

        // Set document title
        if (entity.getTypeDocument() != null && entity.getDocumentId() != null) {
            response.setDocumentTitle(documentService.getDocumentTitle(entity.getTypeDocument(), entity.getDocumentId()));
        }

        // Format date
        if (entity.getDateAction() != null) {
            response.setFormattedDate(entity.getDateAction().format(DATE_FORMATTER));
        }

        return response;
    }
}