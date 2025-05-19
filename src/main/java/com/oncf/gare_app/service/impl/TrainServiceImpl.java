package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.dto.TrainRequest;
import com.oncf.gare_app.dto.TrainResponse;
import com.oncf.gare_app.entity.Train;
import com.oncf.gare_app.exception.ResourceNotFoundException;
import com.oncf.gare_app.exception.UniqueConstraintViolationException;
import com.oncf.gare_app.mapper.TrainMapper;
import com.oncf.gare_app.repository.TrainRepository;
import com.oncf.gare_app.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {

    private final TrainRepository trainRepository;
    private final TrainMapper trainMapper;

    @Transactional(readOnly = true)
    @Override
    public List<TrainResponse> getAllTrains() {
        return trainRepository.findAll().stream()
                .map(trainMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public TrainResponse getTrainById(Long id) {
        return trainRepository.findById(id)
                .map(trainMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Train non trouvé avec l'id: " + id));
    }

    @Transactional
    @Override
    public TrainResponse createTrain(TrainRequest request) {
        // Check if a train with the same number already exists
        if (trainRepository.existsByNumero(request.getNumero())) {
            throw new UniqueConstraintViolationException("Un train avec le numéro " + request.getNumero() + " existe déjà");
        }

        Train train = trainMapper.toEntity(request);
        train = trainRepository.save(train);

        return trainMapper.toDto(train);
    }

    @Transactional
    @Override
    public TrainResponse updateTrain(Long id, TrainRequest request) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train non trouvé avec l'id: " + id));

        // Check if another train with the same number already exists
        if (!train.getNumero().equals(request.getNumero()) &&
                trainRepository.existsByNumero(request.getNumero())) {
            throw new UniqueConstraintViolationException("Un train avec le numéro " + request.getNumero() + " existe déjà");
        }

        trainMapper.updateEntityFromDto(request, train);
        train = trainRepository.save(train);

        return trainMapper.toDto(train);
    }

    @Transactional
    @Override
    public void deleteTrain(Long id) {
        if (!trainRepository.existsById(id)) {
            throw new ResourceNotFoundException("Train non trouvé avec l'id: " + id);
        }

        trainRepository.deleteById(id);
    }
}