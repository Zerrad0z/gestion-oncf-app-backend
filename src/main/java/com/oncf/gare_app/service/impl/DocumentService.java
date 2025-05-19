package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.entity.LettreSommationBillet;
import com.oncf.gare_app.entity.LettreSommationCarte;
import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.entity.RapportM;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.repository.LettreSommationBilletRepository;
import com.oncf.gare_app.repository.LettreSommationCarteRepository;
import com.oncf.gare_app.repository.RapportMRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final LettreSommationBilletRepository lettreSommationBilletRepository;
    private final LettreSommationCarteRepository lettreSommationCarteRepository;
    private final RapportMRepository rapportMRepository;

    /**
     * Gets the associated document for a piece jointe based on its type and ID
     */
    public Object getDocumentForPieceJointe(PieceJointe pieceJointe) {
        TypeDocumentEnum typeDocument = pieceJointe.getTypeDocument();
        Long documentId = pieceJointe.getDocumentId();

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

    /**
     * Gets the associated document title or identifier for display purposes
     */
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
}