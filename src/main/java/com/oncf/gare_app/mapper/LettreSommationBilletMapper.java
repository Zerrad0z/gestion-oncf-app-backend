package com.oncf.gare_app.mapper;

import com.oncf.gare_app.dto.LettreSommationBilletRequest;
import com.oncf.gare_app.dto.LettreSommationBilletResponse;
import com.oncf.gare_app.entity.ACT;
import com.oncf.gare_app.entity.Gare;
import com.oncf.gare_app.entity.LettreSommationBillet;
import com.oncf.gare_app.entity.Train;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.repository.ACTRepository;
import com.oncf.gare_app.repository.GareRepository;
import com.oncf.gare_app.repository.TrainRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

@Mapper(componentModel = "spring",
        uses = {ACTMapper.class, TrainMapper.class, GareMapper.class, UtilisateurMapper.class, PieceJointeMapper.class})
public abstract class LettreSommationBilletMapper {

    @Autowired
    protected ACTRepository actRepository;

    @Autowired
    protected TrainRepository trainRepository;

    @Autowired
    protected GareRepository gareRepository;

    @Mapping(target = "piecesJointes", ignore = true)
    public abstract LettreSommationBilletResponse toDto(LettreSommationBillet entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "act", source = "actId", qualifiedByName = "mapBilletActId")
    @Mapping(target = "train", source = "trainId", qualifiedByName = "mapBilletTrainId")
    @Mapping(target = "gare", source = "gareId", qualifiedByName = "mapBilletGareId")
    @Mapping(target = "utilisateur", expression = "java(getCurrentUser())")
    @Mapping(target = "dateCreationSysteme", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "piecesJointes", ignore = true)
    public abstract LettreSommationBillet toEntity(LettreSommationBilletRequest request);

    @Named("mapBilletActId")
    protected ACT mapBilletActId(Long actId) {
        return actRepository.findById(actId)
                .orElseThrow(() -> new RuntimeException("ACT non trouvé avec l'id: " + actId));
    }

    @Named("mapBilletTrainId")
    protected Train mapBilletTrainId(Long trainId) {
        return trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train non trouvé avec l'id: " + trainId));
    }

    @Named("mapBilletGareId")
    protected Gare mapBilletGareId(Long gareId) {
        return gareRepository.findById(gareId)
                .orElseThrow(() -> new RuntimeException("Gare non trouvée avec l'id: " + gareId));
    }

    protected UtilisateurSysteme getCurrentUser() {
        return (UtilisateurSysteme) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "act", source = "actId", qualifiedByName = "mapBilletActId")
    @Mapping(target = "train", source = "trainId", qualifiedByName = "mapBilletTrainId")
    @Mapping(target = "gare", source = "gareId", qualifiedByName = "mapBilletGareId")
    @Mapping(target = "dateCreationSysteme", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "piecesJointes", ignore = true)
    public abstract void updateEntityFromDto(LettreSommationBilletRequest request, @MappingTarget LettreSommationBillet entity);
}