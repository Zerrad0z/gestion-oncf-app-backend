package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.dto.AntenneRequestDto;
import com.oncf.gare_app.dto.AntenneResponseDto;
import com.oncf.gare_app.entity.Antenne;
import com.oncf.gare_app.exception.AntenneAlreadyExistsException;
import com.oncf.gare_app.exception.AntenneNotFoundException;
import com.oncf.gare_app.exception.SectionNotFoundException;
import com.oncf.gare_app.mapper.AntenneMapper;
import com.oncf.gare_app.repository.AntenneRepository;
import com.oncf.gare_app.repository.SectionRepository;
import com.oncf.gare_app.service.AntenneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AntenneServiceImpl implements AntenneService {

    private final AntenneRepository antenneRepository;
    private final SectionRepository sectionRepository;
    private final AntenneMapper antenneMapper;

    @Override
    public AntenneResponseDto createAntenne(AntenneRequestDto antenneDto) {
        log.info("Création d'une nouvelle antenne avec le nom: {} dans la section ID: {}",
                antenneDto.getNom(), antenneDto.getSectionId());

        // Vérifier si la section existe
        if (!sectionRepository.existsById(antenneDto.getSectionId())) {
            log.error("Section introuvable avec l'ID: {}", antenneDto.getSectionId());
            throw new SectionNotFoundException(antenneDto.getSectionId());
        }

        // Vérifier si une antenne avec le même nom existe déjà dans la même section
        if (antenneRepository.existsByNomAndSectionId(antenneDto.getNom(), antenneDto.getSectionId())) {
            log.error("Une antenne avec le nom '{}' existe déjà dans la section ID: {}",
                    antenneDto.getNom(), antenneDto.getSectionId());
            throw new AntenneAlreadyExistsException(antenneDto.getNom(), antenneDto.getSectionId());
        }

        // Mapper le DTO vers l'entité
        Antenne antenne = antenneMapper.toEntity(antenneDto);

        // Sauvegarder l'antenne
        Antenne savedAntenne = antenneRepository.save(antenne);
        log.info("Antenne créée avec succès avec l'ID: {}", savedAntenne.getId());

        // Mapper l'entité vers le DTO de réponse
        return antenneMapper.toDto(savedAntenne);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AntenneResponseDto> getAllAntennes() {
        log.info("Récupération de toutes les antennes");
        List<Antenne> antennes = antenneRepository.findAll();
        return antenneMapper.toDtoList(antennes);
    }

    @Override
    @Transactional(readOnly = true)
    public AntenneResponseDto getAntenneById(Long id) {
        log.info("Récupération de l'antenne avec l'ID: {}", id);
        Antenne antenne = antenneRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Antenne introuvable avec l'ID: {}", id);
                    return new AntenneNotFoundException(id);
                });
        return antenneMapper.toDto(antenne);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AntenneResponseDto> getAntennesBySection(Long sectionId) {
        log.info("Récupération des antennes pour la section ID: {}", sectionId);

        // Vérifier si la section existe
        if (!sectionRepository.existsById(sectionId)) {
            log.error("Section introuvable avec l'ID: {}", sectionId);
            throw new SectionNotFoundException(sectionId);
        }

        List<Antenne> antennes = antenneRepository.findBySectionId(sectionId);
        return antenneMapper.toDtoList(antennes);
    }

    @Override
    public AntenneResponseDto updateAntenne(Long id, AntenneRequestDto antenneDto) {
        log.info("Mise à jour de l'antenne avec l'ID: {}", id);

        // Vérifier si l'antenne existe
        Antenne existingAntenne = antenneRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Antenne introuvable avec l'ID: {}", id);
                    return new AntenneNotFoundException(id);
                });

        // Vérifier si la section existe
        if (!sectionRepository.existsById(antenneDto.getSectionId())) {
            log.error("Section introuvable avec l'ID: {}", antenneDto.getSectionId());
            throw new SectionNotFoundException(antenneDto.getSectionId());
        }

        // Vérifier si le nouveau nom existe déjà pour une autre antenne dans la même section
        if (!existingAntenne.getNom().equals(antenneDto.getNom()) ||
                !existingAntenne.getSection().getId().equals(antenneDto.getSectionId())) {
            if (antenneRepository.existsByNomAndSectionId(antenneDto.getNom(), antenneDto.getSectionId())) {
                log.error("Une antenne avec le nom '{}' existe déjà dans la section ID: {}",
                        antenneDto.getNom(), antenneDto.getSectionId());
                throw new AntenneAlreadyExistsException(antenneDto.getNom(), antenneDto.getSectionId());
            }
        }

        // Mettre à jour les propriétés de l'antenne
        antenneMapper.updateEntityFromDto(antenneDto, existingAntenne);

        // Sauvegarder les modifications
        Antenne updatedAntenne = antenneRepository.save(existingAntenne);
        log.info("Antenne mise à jour avec succès");

        // Mapper l'entité vers le DTO de réponse
        return antenneMapper.toDto(updatedAntenne);
    }

    @Override
    public void deleteAntenne(Long id) {
        log.info("Suppression de l'antenne avec l'ID: {}", id);

        // Vérifier si l'antenne existe
        if (!antenneRepository.existsById(id)) {
            log.error("Antenne introuvable avec l'ID: {}", id);
            throw new AntenneNotFoundException(id);
        }

        // Supprimer l'antenne
        antenneRepository.deleteById(id);
        log.info("Antenne supprimée avec succès");
    }
}