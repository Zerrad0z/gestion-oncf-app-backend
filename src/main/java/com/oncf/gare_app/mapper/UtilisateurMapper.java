package com.oncf.gare_app.mapper;

import com.oncf.gare_app.dto.UtilisateurRequest;
import com.oncf.gare_app.dto.UtilisateurResponse;
import com.oncf.gare_app.dto.UtilisateurUpdateRequest;
import com.oncf.gare_app.entity.ACT;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UtilisateurMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "derniereConnexion", ignore = true)
    @Mapping(target = "motDePasseHash", expression = "java(passwordEncoder.encode(request.getMotDePasse()))")
    @Mapping(target = "act", source = "actId", qualifiedByName = "actIdToAct")
    public abstract UtilisateurSysteme toEntity(UtilisateurRequest request);

    @Mapping(target = "actId", source = "act.id")
    @Mapping(target = "actNomPrenom", source = "act.nomPrenom")
    public abstract UtilisateurResponse toDto(UtilisateurSysteme entity);

    @Mapping(target = "id", ignore = false)
    public abstract UtilisateurSysteme clone(UtilisateurSysteme entity);

    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateDerniereModification", ignore = true)
    @Mapping(target = "motDePasseHash", expression = "java(updateRequest.getMotDePasse() != null ? passwordEncoder.encode(updateRequest.getMotDePasse()) : entity.getMotDePasseHash())")
    @Mapping(target = "act", source = "updateRequest.actId", qualifiedByName = "actIdToAct")
    public abstract void updateEntityFromDto(UtilisateurUpdateRequest updateRequest, @MappingTarget UtilisateurSysteme entity);

    @Named("actIdToAct")
    protected ACT actIdToAct(Long actId) {
        if (actId == null) {
            return null;
        }

        ACT act = new ACT();
        act.setId(actId);
        return act;
    }
}