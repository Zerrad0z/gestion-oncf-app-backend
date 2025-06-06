package com.oncf.gare_app.service;

import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.enums.TypeDocumentEnum;

import java.util.List;

public interface DocumentService {

    Object getDocumentForPieceJointe(PieceJointe pieceJointe);

    String getDocumentTitle(PieceJointe pieceJointe);

    String getDocumentTitle(TypeDocumentEnum typeDocument, Long documentId);

    boolean documentExists(TypeDocumentEnum typeDocument, Long documentId);

    List<PieceJointe> getPiecesJointesForDocument(TypeDocumentEnum typeDocument, Long documentId);

    void addPieceJointeToDocument(PieceJointe pieceJointe, TypeDocumentEnum typeDocument, Long documentId);

    void removePiecesJointesFromDocument(TypeDocumentEnum typeDocument, Long documentId);
}