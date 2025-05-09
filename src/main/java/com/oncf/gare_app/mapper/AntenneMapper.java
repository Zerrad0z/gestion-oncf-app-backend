package com.oncf.gare_app.mapper;

import com.oncf.gare_app.dto.AntenneRequestDto;
import com.oncf.gare_app.dto.AntenneResponseDto;
import com.oncf.gare_app.entity.Antenne;
import com.oncf.gare_app.entity.Section;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AntenneMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "section", source = "sectionId", qualifiedByName = "sectionIdToSection")
    @Mapping(target = "agents", ignore = true)
    Antenne toEntity(AntenneRequestDto dto);

    @Mapping(target = "sectionId", source = "section.id")
    @Mapping(target = "sectionNom", source = "section.nom")
    @Mapping(target = "nombreAgents", expression = "java(antenne.getAgents() != null ? antenne.getAgents().size() : 0)")
    AntenneResponseDto toDto(Antenne antenne);

    List<AntenneResponseDto> toDtoList(List<Antenne> antennes);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "section", source = "sectionId", qualifiedByName = "sectionIdToSection")
    @Mapping(target = "agents", ignore = true)
    void updateEntityFromDto(AntenneRequestDto dto, @MappingTarget Antenne antenne);

    @Named("sectionIdToSection")
    default Section sectionIdToSection(Long sectionId) {
        if (sectionId == null) {
            return null;
        }
        Section section = new Section();
        section.setId(sectionId);
        return section;
    }
}