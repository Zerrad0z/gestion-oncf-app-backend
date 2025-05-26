package com.oncf.gare_app.repository;

import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PieceJointeRepository extends JpaRepository<PieceJointe, Long> {

    /**
     * Find all pieces jointes for a specific document type and document ID
     */
    List<PieceJointe> findByTypeDocumentAndDocumentId(TypeDocumentEnum typeDocument, Long documentId);

    /**
     * Find all pieces jointes for a specific document type
     */
    List<PieceJointe> findAllByTypeDocument(TypeDocumentEnum typeDocument);

    /**
     * Delete all pieces jointes for a specific document
     */
    void deleteByTypeDocumentAndDocumentId(TypeDocumentEnum typeDocument, Long documentId);

    /**
     * Get total file size for a specific document
     */
    @Query("SELECT COALESCE(SUM(p.taille), 0) FROM PieceJointe p WHERE p.typeDocument = :typeDocument AND p.documentId = :documentId")
    Long getTotalSizeByTypeDocumentAndDocumentId(@Param("typeDocument") TypeDocumentEnum typeDocument, @Param("documentId") Long documentId);

    /**
     * Check if any pieces jointes exist for a document
     */
    boolean existsByTypeDocumentAndDocumentId(TypeDocumentEnum typeDocument, Long documentId);

    /**
     * Count pieces jointes for a document
     */
    long countByTypeDocumentAndDocumentId(TypeDocumentEnum typeDocument, Long documentId);
}