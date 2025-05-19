package com.oncf.gare_app.mapper;

import com.oncf.gare_app.dto.ACTRequest;
import com.oncf.gare_app.dto.ACTResponse;
import com.oncf.gare_app.entity.ACT;
import com.oncf.gare_app.entity.Antenne;
import com.oncf.gare_app.repository.ACTRepository;
import com.oncf.gare_app.repository.AntenneRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {AntenneMapper.class})
public abstract class ACTMapper {

    @Autowired
    protected AntenneRepository antenneRepository;

    @Autowired
    protected ACTRepository actRepository;

    public abstract ACTResponse toDto(ACT entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "antenne", source = "antenneId", qualifiedByName = "antenneIdToAntenneEntity")
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "lettresSommationBillet", ignore = true)
    @Mapping(target = "lettresSommationCarte", ignore = true)
    @Mapping(target = "rapportsM", ignore = true)
    @Mapping(target = "utilisateurSysteme", ignore = true)
    public abstract ACT toEntity(ACTRequest request);

    @Named("antenneIdToAntenneEntity")
    protected Antenne antenneIdToAntenneEntity(Long antenneId) {
        return antenneRepository.findById(antenneId)
                .orElseThrow(() -> new RuntimeException("Antenne non trouvée avec l'id: " + antenneId));
    }

    @Named("mapActIdToACTEntity")
    public ACT mapActIdToACTEntity(Long actId) {
        if (actId == null) {
            return null;
        }
        return actRepository.findById(actId)
                .orElseThrow(() -> new RuntimeException("ACT non trouvé avec l'id: " + actId));
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "antenne", source = "antenneId", qualifiedByName = "antenneIdToAntenneEntity")
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "lettresSommationBillet", ignore = true)
    @Mapping(target = "lettresSommationCarte", ignore = true)
    @Mapping(target = "rapportsM", ignore = true)
    @Mapping(target = "utilisateurSysteme", ignore = true)
    public abstract void updateEntityFromDto(ACTRequest request, @MappingTarget ACT entity);
}