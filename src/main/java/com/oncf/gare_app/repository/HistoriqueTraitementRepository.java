package com.oncf.gare_app.repository;

import com.oncf.gare_app.entity.HistoriqueTraitement;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoriqueTraitementRepository extends JpaRepository<HistoriqueTraitement, Long> {

    List<HistoriqueTraitement> findByTypeDocumentAndDocumentIdOrderByDateActionDesc(
            TypeDocumentEnum typeDocument, Long documentId);

    List<HistoriqueTraitement> findByUtilisateurIdOrderByDateActionDesc(Long utilisateurId);

    List<HistoriqueTraitement> findByActionOrderByDateActionDesc(String action);

    Page<HistoriqueTraitement> findByTypeDocumentAndDocumentId(
            TypeDocumentEnum typeDocument, Long documentId, Pageable pageable);

    Page<HistoriqueTraitement> findByUtilisateurId(Long utilisateurId, Pageable pageable);

    Page<HistoriqueTraitement> findByAction(String action, Pageable pageable);

    Page<HistoriqueTraitement> findByDateActionBetween(
            LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);

    @Query("SELECT h FROM HistoriqueTraitement h WHERE " +
            "(:typeDocument IS NULL OR h.typeDocument = :typeDocument) AND " +
            "(:documentId IS NULL OR h.documentId = :documentId) AND " +
            "(:utilisateurId IS NULL OR h.utilisateur.id = :utilisateurId) AND " +
            "(:action IS NULL OR h.action LIKE %:action%) AND " +
            "(:dateDebut IS NULL OR h.dateAction >= :dateDebut) AND " +
            "(:dateFin IS NULL OR h.dateAction <= :dateFin) " +
            "ORDER BY h.dateAction DESC")
    Page<HistoriqueTraitement> search(
            @Param("typeDocument") TypeDocumentEnum typeDocument,
            @Param("documentId") Long documentId,
            @Param("utilisateurId") Long utilisateurId,
            @Param("action") String action,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            Pageable pageable);
}