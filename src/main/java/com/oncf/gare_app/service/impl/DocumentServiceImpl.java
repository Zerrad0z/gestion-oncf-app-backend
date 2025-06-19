package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.entity.LettreSommationBillet;
import com.oncf.gare_app.entity.LettreSommationCarte;
import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.entity.RapportM;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.repository.LettreSommationBilletRepository;
import com.oncf.gare_app.repository.LettreSommationCarteRepository;
import com.oncf.gare_app.repository.RapportMRepository;
import com.oncf.gare_app.repository.PieceJointeRepository;
import com.oncf.gare_app.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final LettreSommationBilletRepository lettreSommationBilletRepository;
    private final LettreSommationCarteRepository lettreSommationCarteRepository;
    private final RapportMRepository rapportMRepository;
    private final PieceJointeRepository pieceJointeRepository;


    @Override
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


    @Override
    public String getDocumentTitle(PieceJointe pieceJointe) {
        TypeDocumentEnum typeDocument = pieceJointe.getTypeDocument();
        Long documentId = pieceJointe.getDocumentId();
        return getDocumentTitle(typeDocument, documentId);
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
                return rapport != null ? "Rapport M #" + rapport.getId() + " - " + rapport.getReferences() + " (" + rapport.getObjet() + ")" : null;

            default:
                return null;
        }
    }


    @Override
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
    public List<PieceJointe> getPiecesJointesForDocument(TypeDocumentEnum typeDocument, Long documentId) {
        return pieceJointeRepository.findByTypeDocumentAndDocumentId(typeDocument, documentId);
    }


    @Override
    @Transactional
    public void addPieceJointeToDocument(PieceJointe pieceJointe, TypeDocumentEnum typeDocument, Long documentId) {
        pieceJointeRepository.save(pieceJointe);
    }


    @Override
    @Transactional
    public void removePiecesJointesFromDocument(TypeDocumentEnum typeDocument, Long documentId) {
        List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(typeDocument, documentId);
        pieceJointeRepository.deleteAll(piecesJointes);
    }
}