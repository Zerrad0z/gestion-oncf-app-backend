package com.oncf.gare_app.service;


import com.oncf.gare_app.dto.SectionRequestDto;
import com.oncf.gare_app.dto.SectionResponseDto;

import java.util.List;

public interface SectionService {

    /**
     * Crée une nouvelle section
     * @param sectionDto les données de la section à créer
     * @return la section créée
     */
    SectionResponseDto createSection(SectionRequestDto sectionDto);

    /**
     * Récupère toutes les sections
     * @return la liste des sections
     */
    List<SectionResponseDto> getAllSections();

    /**
     * Récupère une section par son ID
     * @param id l'identifiant de la section
     * @return la section correspondante
     */
    SectionResponseDto getSectionById(Long id);

    /**
     * Met à jour une section existante
     * @param id l'identifiant de la section à mettre à jour
     * @param sectionDto les nouvelles données de la section
     * @return la section mise à jour
     */
    SectionResponseDto updateSection(Long id, SectionRequestDto sectionDto);

    /**
     * Supprime une section par son ID
     * @param id l'identifiant de la section à supprimer
     */
    void deleteSection(Long id);
}