package com.oncf.gare_app.repository;

import com.oncf.gare_app.entity.LettreSommationBillet;
import com.oncf.gare_app.enums.StatutEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LettreSommationBilletRepository extends JpaRepository<LettreSommationBillet, Long> {

    List<LettreSommationBillet> findByActId(Long actId);

    List<LettreSommationBillet> findByGareId(Long gareId);

    List<LettreSommationBillet> findByTrainId(Long trainId);

    List<LettreSommationBillet> findByStatut(StatutEnum statut);

    List<LettreSommationBillet> findByDateInfractionBetween(LocalDate dateDebut, LocalDate dateFin);

    List<LettreSommationBillet> findByDateCreationBetween(LocalDate dateDebut, LocalDate dateFin);

    @Query("SELECT l FROM LettreSommationBillet l WHERE " +
            "(:actId IS NULL OR l.act.id = :actId) AND " +
            "(:gareId IS NULL OR l.gare.id = :gareId) AND " +
            "(:trainId IS NULL OR l.train.id = :trainId) AND " +
            "(:statut IS NULL OR l.statut = :statut) AND " +
            "(:numeroBillet IS NULL OR l.numeroBillet LIKE %:numeroBillet%) AND " +
            "(:dateDebut IS NULL OR l.dateInfraction >= :dateDebut) AND " +
            "(:dateFin IS NULL OR l.dateInfraction <= :dateFin)")
    List<LettreSommationBillet> search(
            @Param("actId") Long actId,
            @Param("gareId") Long gareId,
            @Param("trainId") Long trainId,
            @Param("statut") StatutEnum statut,
            @Param("numeroBillet") String numeroBillet,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    @Query("SELECT COUNT(l) FROM LettreSommationBillet l WHERE l.statut = :statut")
    long countByStatut(@Param("statut") StatutEnum statut);

    @Query("SELECT COUNT(l) FROM LettreSommationBillet l WHERE l.dateCreation BETWEEN :dateDebut AND :dateFin")
    long countByDateCreationBetween(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    boolean existsByNumeroBillet(String numeroBillet);
}