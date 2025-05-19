package com.oncf.gare_app.mapper;

import com.oncf.gare_app.dto.RapportMRequest;
import com.oncf.gare_app.dto.RapportMResponse;
import com.oncf.gare_app.entity.ACT;
import com.oncf.gare_app.entity.RapportM;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.repository.ACTRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

@Mapper(componentModel = "spring",
        uses = {ACTMapper.class, UtilisateurMapper.class, PieceJointeMapper.class})
public abstract class RapportMMapper {

    @Autowired
    protected ACTRepository actRepository;

    @Mapping(target = "piecesJointes", ignore = true)
    public abstract RapportMResponse toDto(RapportM entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "act", source = "actId", qualifiedByName = "mapRapportActId")
    @Mapping(target = "utilisateur", expression = "java(getCurrentUser())")
    @Mapping(target = "dateCreationSysteme", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "piecesJointes", ignore = true)
    public abstract RapportM toEntity(RapportMRequest request);

    @Named("mapRapportActId")
    protected ACT mapRapportActId(Long actId) {
        return actRepository.findById(actId)
                .orElseThrow(() -> new RuntimeException("ACT non trouvé avec l'id: " + actId));
    }

    protected UtilisateurSysteme getCurrentUser() {
        return (UtilisateurSysteme) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "act", source = "actId", qualifiedByName = "mapRapportActId")
    @Mapping(target = "dateCreationSysteme", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "piecesJointes", ignore = true)
    public abstract void updateEntityFromDto(RapportMRequest request, @MappingTarget RapportM entity);
}