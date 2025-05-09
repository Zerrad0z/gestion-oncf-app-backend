package com.oncf.gare_app.repository;

import com.oncf.gare_app.entity.Antenne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AntenneRepository extends JpaRepository<Antenne, Long> {

    List<Antenne> findBySectionId(Long sectionId);

    boolean existsByNomAndSectionId(String nom, Long sectionId);

    Optional<Antenne> findByNomAndSectionId(String nom, Long sectionId);
}