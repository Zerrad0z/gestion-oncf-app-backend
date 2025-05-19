package com.oncf.gare_app.service;

import com.oncf.gare_app.dto.ACTRequest;
import com.oncf.gare_app.dto.ACTResponse;

import java.util.List;

public interface ACTService {

    List<ACTResponse> getAllACTs();

    List<ACTResponse> getACTsByAntenneId(Long antenneId);

    ACTResponse getACTById(Long id);

    ACTResponse getACTByMatricule(String matricule);

    ACTResponse createACT(ACTRequest request);

    ACTResponse updateACT(Long id, ACTRequest request);

    void deleteACT(Long id);
}