package com.oncf.gare_app.service;

import com.oncf.gare_app.dto.GareRequest;
import com.oncf.gare_app.dto.GareResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GareService {
    @Transactional(readOnly = true)
    List<GareResponse> getAllGares();

    @Transactional(readOnly = true)
    GareResponse getGareById(Long id);

    @Transactional
    GareResponse createGare(GareRequest request);

    @Transactional
    GareResponse updateGare(Long id, GareRequest request);

    @Transactional
    void deleteGare(Long id);
}
