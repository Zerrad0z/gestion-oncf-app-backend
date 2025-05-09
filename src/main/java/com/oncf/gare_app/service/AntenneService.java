package com.oncf.gare_app.service;


import com.oncf.gare_app.dto.AntenneRequestDto;
import com.oncf.gare_app.dto.AntenneResponseDto;

import java.util.List;

public interface AntenneService {

    /**
     * Crée une nouvelle antenne
     * @param antenneDto les données de l'antenne à créer
     * @return l'antenne créée
     */
    AntenneResponseDto createAntenne(AntenneRequestDto antenneDto);

    /**
     * Récupère toutes les antennes
     * @return la liste des antennes
     */
    List<AntenneResponseDto> getAllAntennes();

    /**
     * Récupère une antenne par son ID
     * @param id l'identifiant de l'antenne
     * @return l'antenne correspondante
     */
    AntenneResponseDto getAntenneById(Long id);

    /**
     * Récupère toutes les antennes d'une section
     * @param sectionId l'identifiant de la section
     * @return la liste des antennes de la section
     */
    List<AntenneResponseDto> getAntennesBySection(Long sectionId);

    /**
     * Met à jour une antenne existante
     * @param id l'identifiant de l'antenne à mettre à jour
     * @param antenneDto les nouvelles données de l'antenne
     * @return l'antenne mise à jour
     */
    AntenneResponseDto updateAntenne(Long id, AntenneRequestDto antenneDto);

    /**
     * Supprime une antenne par son ID
     * @param id l'identifiant de l'antenne à supprimer
     */
    void deleteAntenne(Long id);
}