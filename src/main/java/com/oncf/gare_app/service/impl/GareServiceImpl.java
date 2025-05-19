package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.dto.GareRequest;
import com.oncf.gare_app.dto.GareResponse;
import com.oncf.gare_app.entity.Gare;
import com.oncf.gare_app.exception.ResourceNotFoundException;
import com.oncf.gare_app.exception.UniqueConstraintViolationException;
import com.oncf.gare_app.mapper.GareMapper;
import com.oncf.gare_app.repository.GareRepository;
import com.oncf.gare_app.service.GareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GareServiceImpl implements GareService {

    private final GareRepository gareRepository;
    private final GareMapper gareMapper;

    @Transactional(readOnly = true)
    @Override
    public List<GareResponse> getAllGares() {
        return gareRepository.findAll().stream()
                .map(gareMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public GareResponse getGareById(Long id) {
        return gareRepository.findById(id)
                .map(gareMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Gare non trouvée avec l'id: " + id));
    }

    @Transactional
    @Override
    public GareResponse createGare(GareRequest request) {
        // Check if a gare with the same name already exists
        if (gareRepository.existsByNom(request.getNom())) {
            throw new UniqueConstraintViolationException("Une gare avec le nom " + request.getNom() + " existe déjà");
        }

        Gare gare = gareMapper.toEntity(request);
        gare = gareRepository.save(gare);

        return gareMapper.toDto(gare);
    }

    @Transactional
    @Override
    public GareResponse updateGare(Long id, GareRequest request) {
        Gare gare = gareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gare non trouvée avec l'id: " + id));

        // Check if another gare with the same name already exists
        if (!gare.getNom().equals(request.getNom()) &&
                gareRepository.existsByNom(request.getNom())) {
            throw new UniqueConstraintViolationException("Une gare avec le nom " + request.getNom() + " existe déjà");
        }

        gareMapper.updateEntityFromDto(request, gare);
        gare = gareRepository.save(gare);

        return gareMapper.toDto(gare);
    }

    @Transactional
    @Override
    public void deleteGare(Long id) {
        if (!gareRepository.existsById(id)) {
            throw new ResourceNotFoundException("Gare non trouvée avec l'id: " + id);
        }

        gareRepository.deleteById(id);
    }
}