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


    List<PieceJointe> findByTypeDocumentAndDocumentId(TypeDocumentEnum typeDocument, Long documentId);

    List<PieceJointe> findAllByTypeDocument(TypeDocumentEnum typeDocument);

    void deleteByTypeDocumentAndDocumentId(TypeDocumentEnum typeDocument, Long documentId);

    @Query("SELECT COALESCE(SUM(p.taille), 0) FROM PieceJointe p WHERE p.typeDocument = :typeDocument AND p.documentId = :documentId")
    Long getTotalSizeByTypeDocumentAndDocumentId(@Param("typeDocument") TypeDocumentEnum typeDocument, @Param("documentId") Long documentId);

    boolean existsByTypeDocumentAndDocumentId(TypeDocumentEnum typeDocument, Long documentId);

    long countByTypeDocumentAndDocumentId(TypeDocumentEnum typeDocument, Long documentId);
}