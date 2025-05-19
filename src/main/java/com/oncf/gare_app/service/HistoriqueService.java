package com.oncf.gare_app.service;

import com.oncf.gare_app.dto.HistoriqueTraitementResponse;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.TypeDocumentEnum;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistoriqueService {

    void trackDocumentAction(TypeDocumentEnum typeDocument, Long documentId,
                             UtilisateurSysteme utilisateur, String action,
                             String details, String ancienStatut, String nouveauStatut);

    void trackDocumentStatusChange(TypeDocumentEnum typeDocument, Long documentId,
                                   UtilisateurSysteme utilisateur, String ancienStatut,
                                   String nouveauStatut, String details);

    void trackDocumentCreation(TypeDocumentEnum typeDocument, Long documentId,
                               UtilisateurSysteme utilisateur);

    void trackDocumentUpdate(TypeDocumentEnum typeDocument, Long documentId,
                             UtilisateurSysteme utilisateur, String details);

    void trackDocumentDeletion(TypeDocumentEnum typeDocument, Long documentId,
                               UtilisateurSysteme utilisateur);

    List<HistoriqueTraitementResponse> getHistoriqueForDocument(TypeDocumentEnum typeDocument, Long documentId);

    List<HistoriqueTraitementResponse> getHistoriqueForUtilisateur(Long utilisateurId);

    List<HistoriqueTraitementResponse> getHistoriqueForAction(String action);

    Page<HistoriqueTraitementResponse> searchHistorique(
            TypeDocumentEnum typeDocument,
            Long documentId,
            Long utilisateurId,
            String action,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            Pageable pageable);
}