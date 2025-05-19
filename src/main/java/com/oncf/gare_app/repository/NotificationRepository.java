package com.oncf.gare_app.repository;

import com.oncf.gare_app.entity.Notification;
import com.oncf.gare_app.enums.TypeNotificationEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByDestinataire_IdOrderByDateCreationDesc(Long destinataireId);

    List<Notification> findByDestinataire_IdAndLueFalseOrderByDateCreationDesc(Long destinataireId);

    long countByDestinataire_IdAndLueFalse(Long destinataireId);

    Page<Notification> findByDestinataire_IdOrderByDateCreationDesc(Long destinataireId, Pageable pageable);

    Page<Notification> findByDestinataire_IdAndLueFalseOrderByDateCreationDesc(Long destinataireId, Pageable pageable);

    Page<Notification> findByTypeOrderByDateCreationDesc(TypeNotificationEnum type, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE " +
            "n.destinataire.id = :destinataireId AND " +
            "(:type IS NULL OR n.type = :type) AND " +
            "(:lue IS NULL OR n.lue = :lue) " +
            "ORDER BY n.dateCreation DESC")
    Page<Notification> search(
            @Param("destinataireId") Long destinataireId,
            @Param("type") TypeNotificationEnum type,
            @Param("lue") Boolean lue,
            Pageable pageable);
}