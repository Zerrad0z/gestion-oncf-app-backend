package com.oncf.gare_app.service;

import com.oncf.gare_app.dto.TrainRequest;
import com.oncf.gare_app.dto.TrainResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TrainService {
    @Transactional(readOnly = true)
    List<TrainResponse> getAllTrains();

    @Transactional(readOnly = true)
    TrainResponse getTrainById(Long id);

    @Transactional
    TrainResponse createTrain(TrainRequest request);

    @Transactional
    TrainResponse updateTrain(Long id, TrainRequest request);

    @Transactional
    void deleteTrain(Long id);
}
