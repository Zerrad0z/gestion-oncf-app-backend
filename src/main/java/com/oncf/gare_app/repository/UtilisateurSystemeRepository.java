package com.oncf.gare_app.repository;

import com.oncf.gare_app.entity.Section;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.RoleUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurSystemeRepository extends JpaRepository<UtilisateurSysteme, Long> {
    Optional<UtilisateurSysteme> findByNomUtilisateur(String nomUtilisateur);
    Optional<UtilisateurSysteme> findByEmail(String email);
    boolean existsByNomUtilisateur(String nomUtilisateur);
    boolean existsByEmail(String email);
    boolean existsByMatricule(String matricule);
    /**
     * Find users by role and section
     */
    @Query("SELECT u FROM UtilisateurSysteme u " +
            "JOIN u.act a " +
            "JOIN a.antenne ant " +
            "WHERE u.role = :role AND ant.section = :section")
    List<UtilisateurSysteme> findByRoleAndAntenneSection(
            @Param("role") RoleUtilisateur role,
            @Param("section") Section section);

    /**
     * Find users by role and section ID
     */
    @Query("SELECT u FROM UtilisateurSysteme u " +
            "JOIN u.act a " +
            "JOIN a.antenne ant " +
            "WHERE u.role = :role AND ant.section.id = :sectionId")
    List<UtilisateurSysteme> findByRoleAndAntenneSectionId(
            @Param("role") RoleUtilisateur role,
            @Param("sectionId") Long sectionId);
}