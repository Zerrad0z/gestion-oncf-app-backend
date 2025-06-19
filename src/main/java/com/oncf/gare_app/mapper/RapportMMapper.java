package com.oncf.gare_app.mapper;

import com.oncf.gare_app.dto.RapportMRequest;
import com.oncf.gare_app.dto.RapportMResponse;
import com.oncf.gare_app.dto.PieceJointeResponse;
import com.oncf.gare_app.entity.ACT;
import com.oncf.gare_app.entity.RapportM;
import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.entity.Train;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.repository.ACTRepository;
import com.oncf.gare_app.repository.TrainRepository;
import com.oncf.gare_app.repository.PieceJointeRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {ACTMapper.class, TrainMapper.class, UtilisateurMapper.class, PieceJointeMapper.class})
public abstract class RapportMMapper {

    @Autowired
    protected ACTRepository actRepository;

    @Autowired
    protected TrainRepository trainRepository;

    @Autowired
    protected PieceJointeRepository pieceJointeRepository;

    @Autowired
    protected PieceJointeMapper pieceJointeMapper;

    @Mapping(target = "piecesJointes", ignore = true)
    public abstract RapportMResponse toDto(RapportM entity);

    @AfterMapping
    protected void loadPiecesJointes(@MappingTarget RapportMResponse response, RapportM entity) {
        if (entity.getId() != null) {
            List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                    TypeDocumentEnum.RAPPORT_M, entity.getId());

            if (piecesJointes != null && !piecesJointes.isEmpty()) {
                List<PieceJointeResponse> piecesJointesDto = piecesJointes.stream()
                        .map(pieceJointeMapper::toDto)
                        .collect(Collectors.toList());

                response.setPiecesJointes(piecesJointesDto);

                System.out.println("✅ Loaded " + piecesJointesDto.size() + " pieces jointes for RapportM ID: " + entity.getId());
            } else {
                System.out.println("⚠️ No pieces jointes found for RapportM ID: " + entity.getId());
            }
        }
    }

    @AfterMapping
    protected void debugMapping(@MappingTarget RapportMResponse response, RapportM entity) {
        System.out.println("=== MAPPER DEBUG ===");
        System.out.println("Entity ID: " + entity.getId());
        System.out.println("Entity ACT: " + (entity.getAct() != null ? entity.getAct().getNomPrenom() : "NULL"));
        System.out.println("Entity Train: " + (entity.getTrain() != null ? entity.getTrain().getNumero() : "NULL"));
        System.out.println("Response ACT: " + (response.getAct() != null ? response.getAct().getNomPrenom() : "NULL"));
        System.out.println("Response Train: " + (response.getTrain() != null ? response.getTrain().getNumero() : "NULL"));
        System.out.println("==================");
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "act", source = "actId", qualifiedByName = "mapRapportActId")
    @Mapping(target = "train", source = "trainId", qualifiedByName = "mapRapportTrainId")
    @Mapping(target = "utilisateur", ignore = true) // Set manually in service
    @Mapping(target = "dateCreationSysteme", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "piecesJointes", ignore = true)
    public abstract RapportM toEntity(RapportMRequest request);

    @Named("mapRapportActId")
    protected ACT mapRapportActId(Long actId) {
        ACT act = actRepository.findById(actId)
                .orElseThrow(() -> new RuntimeException("ACT non trouvé avec l'id: " + actId));
        System.out.println("✅ Mapped ACT ID " + actId + " to: " + act.getNomPrenom());
        return act;
    }

    @Named("mapRapportTrainId")
    protected Train mapRapportTrainId(Long trainId) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train non trouvé avec l'id: " + trainId));
        System.out.println("✅ Mapped Train ID " + trainId + " to: " + train.getNumero());
        return train;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "act", source = "actId", qualifiedByName = "mapRapportActId")
    @Mapping(target = "train", source = "trainId", qualifiedByName = "mapRapportTrainId")
    @Mapping(target = "dateCreationSysteme", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "piecesJointes", ignore = true)
    public abstract void updateEntityFromDto(RapportMRequest request, @MappingTarget RapportM entity);
}