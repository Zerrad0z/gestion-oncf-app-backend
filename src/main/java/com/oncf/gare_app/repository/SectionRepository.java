package com.oncf.gare_app.repository;

import com.oncf.gare_app.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    boolean existsByNom(String nom);

    Optional<Section> findByNom(String nom);

}