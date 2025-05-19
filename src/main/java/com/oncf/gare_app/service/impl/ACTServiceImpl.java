package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.dto.ACTRequest;
import com.oncf.gare_app.dto.ACTResponse;
import com.oncf.gare_app.entity.ACT;
import com.oncf.gare_app.exception.ResourceNotFoundException;
import com.oncf.gare_app.exception.UniqueConstraintViolationException;
import com.oncf.gare_app.mapper.ACTMapper;
import com.oncf.gare_app.repository.ACTRepository;
import com.oncf.gare_app.service.ACTService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ACTServiceImpl implements ACTService {

    private final ACTRepository actRepository;
    private final ACTMapper actMapper;

    @Transactional(readOnly = true)
    @Override
    public List<ACTResponse> getAllACTs() {
        return actRepository.findAll().stream()
                .map(actMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ACTResponse> getACTsByAntenneId(Long antenneId) {
        return actRepository.findByAntenneId(antenneId).stream()
                .map(actMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ACTResponse getACTById(Long id) {
        return actRepository.findById(id)
                .map(actMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("ACT non trouvé avec l'id: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public ACTResponse getACTByMatricule(String matricule) {
        return actRepository.findByMatricule(matricule)
                .map(actMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("ACT non trouvé avec le matricule: " + matricule));
    }

    @Transactional
    @Override
    public ACTResponse createACT(ACTRequest request) {
        // Check if an ACT with the same matricule already exists
        if (actRepository.existsByMatricule(request.getMatricule())) {
            throw new UniqueConstraintViolationException("Un ACT avec le matricule " + request.getMatricule() + " existe déjà");
        }

        ACT act = actMapper.toEntity(request);
        act = actRepository.save(act);

        return actMapper.toDto(act);
    }

    @Transactional
    @Override
    public ACTResponse updateACT(Long id, ACTRequest request) {
        ACT act = actRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ACT non trouvé avec l'id: " + id));

        // Check if another ACT with the same matricule already exists
        if (!act.getMatricule().equals(request.getMatricule()) &&
                actRepository.existsByMatricule(request.getMatricule())) {
            throw new UniqueConstraintViolationException("Un ACT avec le matricule " + request.getMatricule() + " existe déjà");
        }

        actMapper.updateEntityFromDto(request, act);
        act = actRepository.save(act);

        return actMapper.toDto(act);
    }

    @Transactional
    @Override
    public void deleteACT(Long id) {
        if (!actRepository.existsById(id)) {
            throw new ResourceNotFoundException("ACT non trouvé avec l'id: " + id);
        }

        actRepository.deleteById(id);
    }
}