package com.oncf.gare_app.service;

import com.oncf.gare_app.dto.BulkUpdateStatusRequest;
import com.oncf.gare_app.dto.LettreSommationBilletRequest;
import com.oncf.gare_app.dto.LettreSommationBilletResponse;
import com.oncf.gare_app.enums.StatutEnum;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface LettreSommationBilletService {

    List<LettreSommationBilletResponse> getAllLettresSommationBillet();

    LettreSommationBilletResponse getLettreSommationBilletById(Long id);

    LettreSommationBilletResponse createLettreSommationBillet(LettreSommationBilletRequest request, List<MultipartFile> fichiers);

    LettreSommationBilletResponse updateLettreSommationBillet(Long id, LettreSommationBilletRequest request, List<MultipartFile> fichiers);

    void deleteLettreSommationBillet(Long id);

    List<LettreSommationBilletResponse> searchLettresSommationBillet(
            Long actId,
            Long gareId,
            Long trainId,
            StatutEnum statut,
            String numeroBillet,
            LocalDate dateDebut,
            LocalDate dateFin);

    List<LettreSommationBilletResponse> getLettreSommationBilletByActId(Long actId);

    List<LettreSommationBilletResponse> getLettreSommationBilletByGareId(Long gareId);

    List<LettreSommationBilletResponse> getLettreSommationBilletByTrainId(Long trainId);

    List<LettreSommationBilletResponse> getLettreSommationBilletByStatut(StatutEnum statut);

    List<LettreSommationBilletResponse> getLettreSommationBilletByDateRange(LocalDate dateDebut, LocalDate dateFin);

    boolean existsLettreSommationBilletByNumeroBillet(String numeroBillet);

    List<LettreSommationBilletResponse> updateBulkStatus(BulkUpdateStatusRequest request);
}