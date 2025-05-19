package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.dto.HistoriqueTraitementResponse;
import com.oncf.gare_app.entity.HistoriqueTraitement;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.mapper.HistoriqueTraitementMapper;
import com.oncf.gare_app.repository.HistoriqueTraitementRepository;
import com.oncf.gare_app.service.HistoriqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoriqueServiceImpl implements HistoriqueService {

    private final HistoriqueTraitementRepository historiqueRepository;
    private final HistoriqueTraitementMapper historiqueMapper;

    @Transactional
    @Override
    public void trackDocumentAction(TypeDocumentEnum typeDocument, Long documentId,
                                    UtilisateurSysteme utilisateur, String action,
                                    String details, String ancienStatut, String nouveauStatut) {
        HistoriqueTraitement historique = HistoriqueTraitement.builder()
                .typeDocument(typeDocument)
                .documentId(documentId)
                .utilisateur(utilisateur)
                .action(action)
                .details(details)
                .ancienStatut(ancienStatut)
                .nouveauStatut(nouveauStatut)
                .build();

        historiqueRepository.save(historique);
    }

    @Transactional
    @Override
    public void trackDocumentStatusChange(TypeDocumentEnum typeDocument, Long documentId,
                                          UtilisateurSysteme utilisateur, String ancienStatut,
                                          String nouveauStatut, String details) {
        trackDocumentAction(
                typeDocument,
                documentId,
                utilisateur,
                "Changement de statut",
                details,
                ancienStatut,
                nouveauStatut
        );
    }

    @Transactional
    @Override
    public void trackDocumentCreation(TypeDocumentEnum typeDocument, Long documentId,
                                      UtilisateurSysteme utilisateur) {
        trackDocumentAction(
                typeDocument,
                documentId,
                utilisateur,
                "Création",
                "Document créé",
                null,
                null
        );
    }

    @Transactional
    @Override
    public void trackDocumentUpdate(TypeDocumentEnum typeDocument, Long documentId,
                                    UtilisateurSysteme utilisateur, String details) {
        trackDocumentAction(
                typeDocument,
                documentId,
                utilisateur,
                "Modification",
                details,
                null,
                null
        );
    }

    @Transactional
    @Override
    public void trackDocumentDeletion(TypeDocumentEnum typeDocument, Long documentId,
                                      UtilisateurSysteme utilisateur) {
        trackDocumentAction(
                typeDocument,
                documentId,
                utilisateur,
                "Suppression",
                "Document supprimé",
                null,
                null
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<HistoriqueTraitementResponse> getHistoriqueForDocument(TypeDocumentEnum typeDocument, Long documentId) {
        return historiqueRepository.findByTypeDocumentAndDocumentIdOrderByDateActionDesc(typeDocument, documentId)
                .stream()
                .map(historiqueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<HistoriqueTraitementResponse> getHistoriqueForUtilisateur(Long utilisateurId) {
        return historiqueRepository.findByUtilisateurIdOrderByDateActionDesc(utilisateurId)
                .stream()
                .map(historiqueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<HistoriqueTraitementResponse> getHistoriqueForAction(String action) {
        return historiqueRepository.findByActionOrderByDateActionDesc(action)
                .stream()
                .map(historiqueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<HistoriqueTraitementResponse> searchHistorique(
            TypeDocumentEnum typeDocument, Long documentId, Long utilisateurId,
            String action, LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable) {

        return historiqueRepository.search(typeDocument, documentId, utilisateurId, action, dateDebut, dateFin, pageable)
                .map(historiqueMapper::toDto);
    }
}