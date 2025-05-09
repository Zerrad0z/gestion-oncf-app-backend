package com.oncf.gare_app.mapper;


import com.oncf.gare_app.dto.SectionRequestDto;
import com.oncf.gare_app.dto.SectionResponseDto;
import com.oncf.gare_app.entity.Section;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SectionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "antennes", ignore = true)
    Section toEntity(SectionRequestDto dto);

    @Mapping(target = "nombreAntennes", expression = "java(section.getAntennes() != null ? section.getAntennes().size() : 0)")
    SectionResponseDto toDto(Section section);

    List<SectionResponseDto> toDtoList(List<Section> sections);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "antennes", ignore = true)
    void updateEntityFromDto(SectionRequestDto dto, @MappingTarget Section section);
}