package com.oncf.gare_app.mapper;

import com.oncf.gare_app.dto.TrainRequest;
import com.oncf.gare_app.dto.TrainResponse;
import com.oncf.gare_app.entity.Train;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    Train toEntity(TrainRequest request);

    TrainResponse toDto(Train train);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    void updateEntityFromDto(TrainRequest request, @MappingTarget Train train);
}