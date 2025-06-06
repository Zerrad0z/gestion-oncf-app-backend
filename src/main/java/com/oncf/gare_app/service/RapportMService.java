package com.oncf.gare_app.service;

import com.oncf.gare_app.dto.BulkUpdateStatusRequest;
import com.oncf.gare_app.dto.RapportMRequest;
import com.oncf.gare_app.dto.RapportMResponse;
import com.oncf.gare_app.enums.CategorieRapportEnum;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface RapportMService {

    List<RapportMResponse> getAllRapportsM();

    RapportMResponse getRapportMById(Long id);

    RapportMResponse createRapportM(RapportMRequest request, List<MultipartFile> fichiers);

    RapportMResponse updateRapportM(Long id, RapportMRequest request, List<MultipartFile> fichiers);

    void deleteRapportM(Long id);

    List<RapportMResponse> searchRapportsM(
            Long actId,
            CategorieRapportEnum categorie,
            String references,
            String objet,
            String detail,
            LocalDate dateDebut,
            LocalDate dateFin);

    List<RapportMResponse> getRapportMByActId(Long actId);

    List<RapportMResponse> getRapportMByCategorie(CategorieRapportEnum categorie);

    List<RapportMResponse> getRapportMByDateRange(LocalDate dateDebut, LocalDate dateFin);

    List<RapportMResponse> updateBulk(BulkUpdateStatusRequest request);
}