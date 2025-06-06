package com.oncf.gare_app.repository;

import com.oncf.gare_app.entity.RapportM;
import com.oncf.gare_app.enums.CategorieRapportEnum;
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

    List<RapportM> findByDateEnvoiBetween(LocalDate dateDebut, LocalDate dateFin);

    @Query("SELECT r FROM RapportM r WHERE " +
            "(:actId IS NULL OR r.act.id = :actId) AND " +
            "(:categorie IS NULL OR r.categorie = :categorie) AND " +
            "(:references IS NULL OR r.references LIKE %:references%) AND " +
            "(:objet IS NULL OR r.objet LIKE %:objet%) AND " +
            "(:detail IS NULL OR r.detail LIKE %:detail%) AND " +
            "(:dateDebut IS NULL OR r.dateEnvoi >= :dateDebut) AND " +
            "(:dateFin IS NULL OR r.dateEnvoi <= :dateFin)")
    List<RapportM> search(
            @Param("actId") Long actId,
            @Param("categorie") CategorieRapportEnum categorie,
            @Param("references") String references,
            @Param("objet") String objet,
            @Param("detail") String detail,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    @Query("SELECT COUNT(r) FROM RapportM r WHERE r.categorie = :categorie")
    long countByCategorie(@Param("categorie") CategorieRapportEnum categorie);

    @Query("SELECT COUNT(r) FROM RapportM r WHERE r.dateEnvoi BETWEEN :dateDebut AND :dateFin")
    long countByDateEnvoiBetween(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    @Query("SELECT COUNT(r) FROM RapportM r WHERE r.dateReception BETWEEN :dateDebut AND :dateFin")
    long countByDateReceptionBetween(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
}