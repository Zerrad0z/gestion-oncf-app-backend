package com.oncf.gare_app.mapper;

import com.oncf.gare_app.dto.GareRequest;
import com.oncf.gare_app.dto.GareResponse;
import com.oncf.gare_app.entity.Gare;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GareMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    Gare toEntity(GareRequest request);

    GareResponse toDto(Gare gare);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    void updateEntityFromDto(GareRequest request, @MappingTarget Gare gare);
}