package com.oncf.gare_app.repository;

import com.oncf.gare_app.entity.LettreSommationCarte;
import com.oncf.gare_app.enums.StatutEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LettreSommationCarteRepository extends JpaRepository<LettreSommationCarte, Long> {

    List<LettreSommationCarte> findByActId(Long actId);

    List<LettreSommationCarte> findByGareId(Long gareId);

    List<LettreSommationCarte> findByTrainId(Long trainId);

    List<LettreSommationCarte> findByStatut(StatutEnum statut);

    List<LettreSommationCarte> findByDateInfractionBetween(LocalDate dateDebut, LocalDate dateFin);

    List<LettreSommationCarte> findByDateCreationBetween(LocalDate dateDebut, LocalDate dateFin);

    @Query("SELECT l FROM LettreSommationCarte l WHERE " +
            "(:actId IS NULL OR l.act.id = :actId) AND " +
            "(:gareId IS NULL OR l.gare.id = :gareId) AND " +
            "(:trainId IS NULL OR l.train.id = :trainId) AND " +
            "(:statut IS NULL OR l.statut = :statut) AND " +
            "(:numeroCarte IS NULL OR l.numeroCarte LIKE %:numeroCarte%) AND " +
            "(:typeCarte IS NULL OR l.typeCarte LIKE %:typeCarte%) AND " +
            "(:dateDebut IS NULL OR l.dateInfraction >= :dateDebut) AND " +
            "(:dateFin IS NULL OR l.dateInfraction <= :dateFin)")
    List<LettreSommationCarte> search(
            @Param("actId") Long actId,
            @Param("gareId") Long gareId,
            @Param("trainId") Long trainId,
            @Param("statut") StatutEnum statut,
            @Param("numeroCarte") String numeroCarte,
            @Param("typeCarte") String typeCarte,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    @Query("SELECT COUNT(l) FROM LettreSommationCarte l WHERE l.statut = :statut")
    long countByStatut(@Param("statut") StatutEnum statut);

    @Query("SELECT COUNT(l) FROM LettreSommationCarte l WHERE l.dateCreation BETWEEN :dateDebut AND :dateFin")
    long countByDateCreationBetween(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    boolean existsByNumeroCarte(String numeroCarte);
}