package com.oncf.gare_app.mapper;

import com.oncf.gare_app.dto.LettreSommationCarteRequest;
import com.oncf.gare_app.dto.LettreSommationCarteResponse;
import com.oncf.gare_app.dto.PieceJointeResponse;
import com.oncf.gare_app.entity.ACT;
import com.oncf.gare_app.entity.Gare;
import com.oncf.gare_app.entity.LettreSommationCarte;
import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.entity.Train;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.repository.ACTRepository;
import com.oncf.gare_app.repository.GareRepository;
import com.oncf.gare_app.repository.PieceJointeRepository;
import com.oncf.gare_app.repository.TrainRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {ACTMapper.class, TrainMapper.class, GareMapper.class, UtilisateurMapper.class, PieceJointeMapper.class})
public abstract class LettreSommationCarteMapper {

    @Autowired
    protected ACTRepository actRepository;

    @Autowired
    protected TrainRepository trainRepository;

    @Autowired
    protected GareRepository gareRepository;

    // ADD THESE NEW AUTOWIRED FIELDS
    @Autowired
    protected PieceJointeRepository pieceJointeRepository;

    @Autowired
    protected PieceJointeMapper pieceJointeMapper;

    @Mapping(target = "piecesJointes", ignore = true)
    public abstract LettreSommationCarteResponse toDto(LettreSommationCarte entity);

    // ADD THIS NEW METHOD - This is the key addition!
    @AfterMapping
    protected void loadPiecesJointes(@MappingTarget LettreSommationCarteResponse response, LettreSommationCarte entity) {
        if (entity.getId() != null) {
            // Load pieces jointes using the polymorphic relationship
            List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                    TypeDocumentEnum.LETTRE_CARTE, entity.getId());

            if (piecesJointes != null && !piecesJointes.isEmpty()) {
                List<PieceJointeResponse> piecesJointesDto = piecesJointes.stream()
                        .map(pieceJointeMapper::toDto)
                        .collect(Collectors.toList());

                response.setPiecesJointes(piecesJointesDto);

                // Log for debugging
                System.out.println("Loaded " + piecesJointesDto.size() + " pieces jointes for LettreSommationCarte ID: " + entity.getId());
            }
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "act", source = "actId", qualifiedByName = "mapCarteActId")
    @Mapping(target = "train", source = "trainId", qualifiedByName = "mapCarteTrainId")
    @Mapping(target = "gare", source = "gareId", qualifiedByName = "mapCarteGareId")
    @Mapping(target = "utilisateur", ignore = true) // Set manually in service
    @Mapping(target = "dateCreationSysteme", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "piecesJointes", ignore = true)
    public abstract LettreSommationCarte toEntity(LettreSommationCarteRequest request);

    @Named("mapCarteActId")
    protected ACT mapCarteActId(Long actId) {
        return actRepository.findById(actId)
                .orElseThrow(() -> new RuntimeException("ACT non trouvé avec l'id: " + actId));
    }

    @Named("mapCarteTrainId")
    protected Train mapCarteTrainId(Long trainId) {
        return trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train non trouvé avec l'id: " + trainId));
    }

    @Named("mapCarteGareId")
    protected Gare mapCarteGareId(Long gareId) {
        return gareRepository.findById(gareId)
                .orElseThrow(() -> new RuntimeException("Gare non trouvée avec l'id: " + gareId));
    }

    protected UtilisateurSysteme getCurrentUser() {
        try {
            return (UtilisateurSysteme) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (ClassCastException e) {
            System.out.println("Not authenticated with UtilisateurSysteme - this is expected during testing");
            return null;
        }
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "act", source = "actId", qualifiedByName = "mapCarteActId")
    @Mapping(target = "train", source = "trainId", qualifiedByName = "mapCarteTrainId")
    @Mapping(target = "gare", source = "gareId", qualifiedByName = "mapCarteGareId")
    @Mapping(target = "dateCreationSysteme", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "piecesJointes", ignore = true)
    public abstract void updateEntityFromDto(LettreSommationCarteRequest request, @MappingTarget LettreSommationCarte entity);
}