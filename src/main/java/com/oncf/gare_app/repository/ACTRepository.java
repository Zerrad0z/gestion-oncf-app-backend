package com.oncf.gare_app.repository;

import com.oncf.gare_app.entity.ACT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ACTRepository extends JpaRepository<ACT, Long> {
    Optional<ACT> findByMatricule(String matricule);
    boolean existsByMatricule(String matricule);
    List<ACT> findByAntenneId(Long antenneId);
}