package com.oncf.gare_app.repository;

import com.oncf.gare_app.entity.RapportM;
import com.oncf.gare_app.enums.CategorieRapportEnum;
import com.oncf.gare_app.enums.StatutEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RapportMRepository extends JpaRepository<RapportM, Long> {

    List<RapportM> findByActId(Long actId);

    List<RapportM> findByCategorie(CategorieRapportEnum categorie);

    List<RapportM> findByStatut(StatutEnum statut);

    List<RapportM> findByDateCreationBetween(LocalDate dateDebut, LocalDate dateFin);

    List<RapportM> findByPriorite(Integer priorite);

    @Query("SELECT r FROM RapportM r WHERE " +
            "(:actId IS NULL OR r.act.id = :actId) AND " +
            "(:categorie IS NULL OR r.categorie = :categorie) AND " +
            "(:statut IS NULL OR r.statut = :statut) AND " +
            "(:titre IS NULL OR r.titre LIKE %:titre%) AND " +
            "(:contenu IS NULL OR r.contenu LIKE %:contenu%) AND " +
            "(:priorite IS NULL OR r.priorite = :priorite) AND " +
            "(:dateDebut IS NULL OR r.dateCreation >= :dateDebut) AND " +
            "(:dateFin IS NULL OR r.dateCreation <= :dateFin)")
    List<RapportM> search(
            @Param("actId") Long actId,
            @Param("categorie") CategorieRapportEnum categorie,
            @Param("statut") StatutEnum statut,
            @Param("titre") String titre,
            @Param("contenu") String contenu,
            @Param("priorite") Integer priorite,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    @Query("SELECT COUNT(r) FROM RapportM r WHERE r.statut = :statut")
    long countByStatut(@Param("statut") StatutEnum statut);

    @Query("SELECT COUNT(r) FROM RapportM r WHERE r.categorie = :categorie")
    long countByCategorie(@Param("categorie") CategorieRapportEnum categorie);

    @Query("SELECT COUNT(r) FROM RapportM r WHERE r.dateCreation BETWEEN :dateDebut AND :dateFin")
    long countByDateCreationBetween(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
}