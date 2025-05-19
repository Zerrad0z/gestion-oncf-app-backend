package com.oncf.gare_app.repository;

import com.oncf.gare_app.entity.Gare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GareRepository extends JpaRepository<Gare, Long> {

    boolean existsByNom(String nom);

    Optional<Gare> findByNom(String nom);

    @Query("SELECT g FROM Gare g ORDER BY g.nom")
    List<Gare> findAllOrderByNom();
}