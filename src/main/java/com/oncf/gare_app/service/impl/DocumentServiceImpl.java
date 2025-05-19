package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.entity.LettreSommationBillet;
import com.oncf.gare_app.entity.LettreSommationCarte;
import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.entity.RapportM;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.exception.ResourceNotFoundException;
import com.oncf.gare_app.repository.LettreSommationBilletRepository;
import com.oncf.gare_app.repository.LettreSommationCarteRepository;
import com.oncf.gare_app.repository.PieceJointeRepository;
import com.oncf.gare_app.repository.RapportMRepository;
import com.oncf.gare_app.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final LettreSommationBilletRepository lettreSommationBilletRepository;
    private final LettreSommationCarteRepository lettreSommationCarteRepository;
    private final RapportMRepository rapportMRepository;
    private final PieceJointeRepository pieceJointeRepository;

    @Override
    @Transactional(readOnly = true)
    public Object getDocumentForPieceJointe(PieceJointe pieceJointe) {
        return getDocumentByTypeAndId(pieceJointe.getTypeDocument(), pieceJointe.getDocumentId());
    }

    @Override
    @Transactional(readOnly = true)
    public String getDocumentTitle(PieceJointe pieceJointe) {
        TypeDocumentEnum typeDocument = pieceJointe.getTypeDocument();
        Long documentId = pieceJointe.getDocumentId();

        switch (typeDocument) {
            case LETTRE_BILLET:
                LettreSommationBillet billet = lettreSommationBilletRepository.findById(documentId).orElse(null);
                return billet != null ? "Lettre Billet #" + billet.getId() + " - " + billet.getNumeroBillet() : null;

            case LETTRE_CARTE:
                LettreSommationCarte carte = lettreSommationCarteRepository.findById(documentId).orElse(null);
                return carte != null ? "Lettre Carte #" + carte.getId() + " - " + carte.getNumeroCarte() : null;

            case RAPPORT_M:
                RapportM rapport = rapportMRepository.findById(documentId).orElse(null);
                return rapport != null ? "Rapport #" + rapport.getId() + " - " + rapport.getTitre() : null;

            default:
                return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieceJointe> getPiecesJointesForDocument(TypeDocumentEnum typeDocument, Long documentId) {
        return pieceJointeRepository.findByTypeDocumentAndDocumentId(typeDocument, documentId);
    }

    @Override
    @Transactional
    public void addPieceJointeToDocument(PieceJointe pieceJointe, TypeDocumentEnum typeDocument, Long documentId) {
        // Ensure the document exists
        if (!documentExists(typeDocument, documentId)) {
            throw new ResourceNotFoundException("Document non trouvé: " + typeDocument + " avec ID: " + documentId);
        }

        // Set the document type and ID
        pieceJointe.setTypeDocument(typeDocument);
        pieceJointe.setDocumentId(documentId);

        // Save the piece jointe
        pieceJointeRepository.save(pieceJointe);
    }

    @Override
    @Transactional
    public void removePiecesJointesFromDocument(TypeDocumentEnum typeDocument, Long documentId) {
        pieceJointeRepository.deleteByTypeDocumentAndDocumentId(typeDocument, documentId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean documentExists(TypeDocumentEnum typeDocument, Long documentId) {
        switch (typeDocument) {
            case LETTRE_BILLET:
                return lettreSommationBilletRepository.existsById(documentId);

            case LETTRE_CARTE:
                return lettreSommationCarteRepository.existsById(documentId);

            case RAPPORT_M:
                return rapportMRepository.existsById(documentId);

            default:
                return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getDocumentByTypeAndId(TypeDocumentEnum typeDocument, Long documentId) {
        switch (typeDocument) {
            case LETTRE_BILLET:
                return lettreSommationBilletRepository.findById(documentId).orElse(null);

            case LETTRE_CARTE:
                return lettreSommationCarteRepository.findById(documentId).orElse(null);

            case RAPPORT_M:
                return rapportMRepository.findById(documentId).orElse(null);

            default:
                return null;
        }
    }

    @Override
    public String getDocumentTitle(TypeDocumentEnum typeDocument, Long documentId) {
        switch (typeDocument) {
            case LETTRE_BILLET:
                LettreSommationBillet billet = lettreSommationBilletRepository.findById(documentId).orElse(null);
                return billet != null ? "Lettre Billet #" + billet.getId() + " - " + billet.getNumeroBillet() : null;

            case LETTRE_CARTE:
                LettreSommationCarte carte = lettreSommationCarteRepository.findById(documentId).orElse(null);
                return carte != null ? "Lettre Carte #" + carte.getId() + " - " + carte.getNumeroCarte() : null;

            case RAPPORT_M:
                RapportM rapport = rapportMRepository.findById(documentId).orElse(null);
                return rapport != null ? "Rapport #" + rapport.getId() + " - " + rapport.getTitre() : null;

            default:
                return "Document #" + documentId;
        }
    }
}