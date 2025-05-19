package com.oncf.gare_app.service;

import com.oncf.gare_app.entity.LettreSommationBillet;
import com.oncf.gare_app.entity.LettreSommationCarte;
import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.entity.RapportM;
import com.oncf.gare_app.enums.TypeDocumentEnum;

import java.util.List;

public interface DocumentService {

    /**
     * Gets the associated document for a piece jointe based on its type and ID
     */
    Object getDocumentForPieceJointe(PieceJointe pieceJointe);

    /**
     * Gets the associated document title or identifier for display purposes
     */
    String getDocumentTitle(PieceJointe pieceJointe);

    /**
     * Get all pieces jointes for a specific document
     */
    List<PieceJointe> getPiecesJointesForDocument(TypeDocumentEnum typeDocument, Long documentId);

    /**
     * Adds a piece jointe to a document
     */
    void addPieceJointeToDocument(PieceJointe pieceJointe, TypeDocumentEnum typeDocument, Long documentId);

    /**
     * Removes all pieces jointes from a document
     */
    void removePiecesJointesFromDocument(TypeDocumentEnum typeDocument, Long documentId);

    /**
     * Check if document exists
     */
    boolean documentExists(TypeDocumentEnum typeDocument, Long documentId);

    /**
     * Get document by type and ID
     */
    Object getDocumentByTypeAndId(TypeDocumentEnum typeDocument, Long documentId);

    /**
     * Gets the title of a document based on its type and ID
     */
    String getDocumentTitle(TypeDocumentEnum typeDocument, Long documentId);
}