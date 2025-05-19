package com.oncf.gare_app.repository;

import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PieceJointeRepository extends JpaRepository<PieceJointe, Long> {

    List<PieceJointe> findByTypeDocumentAndDocumentId(TypeDocumentEnum typeDocument, Long documentId);

    @Modifying
    @Query("DELETE FROM PieceJointe p WHERE p.typeDocument = :typeDocument AND p.documentId = :documentId")
    void deleteByTypeDocumentAndDocumentId(@Param("typeDocument") TypeDocumentEnum typeDocument, @Param("documentId") Long documentId);

    @Query("SELECT p FROM PieceJointe p WHERE (:typeDocument IS NULL OR p.typeDocument = :typeDocument)")
    List<PieceJointe> findAllByTypeDocument(@Param("typeDocument") TypeDocumentEnum typeDocument);

    @Query("SELECT COUNT(p) FROM PieceJointe p WHERE p.typeDocument = :typeDocument AND p.documentId = :documentId")
    long countByTypeDocumentAndDocumentId(@Param("typeDocument") TypeDocumentEnum typeDocument, @Param("documentId") Long documentId);

    @Query("SELECT SUM(p.taille) FROM PieceJointe p WHERE p.typeDocument = :typeDocument AND p.documentId = :documentId")
    Long getTotalSizeByTypeDocumentAndDocumentId(@Param("typeDocument") TypeDocumentEnum typeDocument, @Param("documentId") Long documentId);
}