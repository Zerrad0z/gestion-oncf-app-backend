package com.oncf.gare_app.repository;

import com.oncf.gare_app.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {

    boolean existsByNumero(String numero);

    Optional<Train> findByNumero(String numero);

    @Query("SELECT t FROM Train t ORDER BY t.numero")
    List<Train> findAllOrderByNumero();
}