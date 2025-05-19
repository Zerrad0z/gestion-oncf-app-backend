package com.oncf.gare_app.service;

import com.oncf.gare_app.dto.BulkUpdateStatusRequest;
import com.oncf.gare_app.dto.LettreSommationCarteRequest;
import com.oncf.gare_app.dto.LettreSommationCarteResponse;
import com.oncf.gare_app.enums.StatutEnum;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface LettreSommationCarteService {

    List<LettreSommationCarteResponse> getAllLettresSommationCarte();

    LettreSommationCarteResponse getLettreSommationCarteById(Long id);

    LettreSommationCarteResponse createLettreSommationCarte(LettreSommationCarteRequest request, List<MultipartFile> fichiers);

    LettreSommationCarteResponse updateLettreSommationCarte(Long id, LettreSommationCarteRequest request, List<MultipartFile> fichiers);

    void deleteLettreSommationCarte(Long id);

    List<LettreSommationCarteResponse> searchLettresSommationCarte(
            Long actId,
            Long gareId,
            Long trainId,
            StatutEnum statut,
            String numeroCarte,
            String typeCarte,
            LocalDate dateDebut,
            LocalDate dateFin);

    List<LettreSommationCarteResponse> getLettreSommationCarteByActId(Long actId);

    List<LettreSommationCarteResponse> getLettreSommationCarteByGareId(Long gareId);

    List<LettreSommationCarteResponse> getLettreSommationCarteByTrainId(Long trainId);

    List<LettreSommationCarteResponse> getLettreSommationCarteByStatut(StatutEnum statut);

    List<LettreSommationCarteResponse> getLettreSommationCarteByDateRange(LocalDate dateDebut, LocalDate dateFin);

    boolean existsLettreSommationCarteByNumeroCarte(String numeroCarte);

    List<LettreSommationCarteResponse> updateBulkStatus(BulkUpdateStatusRequest request);
}