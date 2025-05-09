package com.oncf.gare_app.service.impl;


import com.oncf.gare_app.dto.SectionRequestDto;
import com.oncf.gare_app.dto.SectionResponseDto;
import com.oncf.gare_app.entity.Section;
import com.oncf.gare_app.exception.SectionAlreadyExistsException;
import com.oncf.gare_app.exception.SectionNotFoundException;
import com.oncf.gare_app.mapper.SectionMapper;
import com.oncf.gare_app.repository.SectionRepository;
import com.oncf.gare_app.service.SectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final SectionMapper sectionMapper;

    @Override
    public SectionResponseDto createSection(SectionRequestDto sectionDto) {
        log.info("Création d'une nouvelle section avec le nom: {}", sectionDto.getNom());

        // Vérifier si une section avec le même nom existe déjà
        if (sectionRepository.existsByNom(sectionDto.getNom())) {
            log.error("Une section avec le nom '{}' existe déjà", sectionDto.getNom());
            throw new SectionAlreadyExistsException(sectionDto.getNom());
        }

        // Mapper le DTO vers l'entité
        Section section = sectionMapper.toEntity(sectionDto);

        // Sauvegarder la section
        Section savedSection = sectionRepository.save(section);
        log.info("Section créée avec succès avec l'ID: {}", savedSection.getId());

        // Mapper l'entité vers le DTO de réponse
        return sectionMapper.toDto(savedSection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SectionResponseDto> getAllSections() {
        log.info("Récupération de toutes les sections");
        List<Section> sections = sectionRepository.findAll();
        return sectionMapper.toDtoList(sections);
    }

    @Override
    @Transactional(readOnly = true)
    public SectionResponseDto getSectionById(Long id) {
        log.info("Récupération de la section avec l'ID: {}", id);
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Section introuvable avec l'ID: {}", id);
                    return new SectionNotFoundException(id);
                });
        return sectionMapper.toDto(section);
    }

    @Override
    public SectionResponseDto updateSection(Long id, SectionRequestDto sectionDto) {
        log.info("Mise à jour de la section avec l'ID: {}", id);

        // Vérifier si la section existe
        Section existingSection = sectionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Section introuvable avec l'ID: {}", id);
                    return new SectionNotFoundException(id);
                });

        // Vérifier si le nouveau nom existe déjà pour une autre section
        if (!existingSection.getNom().equals(sectionDto.getNom()) &&
                sectionRepository.existsByNom(sectionDto.getNom())) {
            log.error("Une section avec le nom '{}' existe déjà", sectionDto.getNom());
            throw new SectionAlreadyExistsException(sectionDto.getNom());
        }

        // Mettre à jour les propriétés de la section
        sectionMapper.updateEntityFromDto(sectionDto, existingSection);

        // Sauvegarder les modifications
        Section updatedSection = sectionRepository.save(existingSection);
        log.info("Section mise à jour avec succès");

        // Mapper l'entité vers le DTO de réponse
        return sectionMapper.toDto(updatedSection);
    }

    @Override
    public void deleteSection(Long id) {
        log.info("Suppression de la section avec l'ID: {}", id);

        // Vérifier si la section existe
        if (!sectionRepository.existsById(id)) {
            log.error("Section introuvable avec l'ID: {}", id);
            throw new SectionNotFoundException(id);
        }

        // Supprimer la section
        sectionRepository.deleteById(id);
        log.info("Section supprimée avec succès");
    }
}