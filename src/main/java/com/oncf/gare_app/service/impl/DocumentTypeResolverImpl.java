package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.entity.LettreSommationBillet;
import com.oncf.gare_app.entity.LettreSommationCarte;
import com.oncf.gare_app.entity.RapportM;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.service.DocumentTypeResolver;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DocumentTypeResolverImpl implements DocumentTypeResolver {

    private static final Map<TypeDocumentEnum, Class<?>> DOCUMENT_TYPE_MAP = new HashMap<>();
    private static final Map<TypeDocumentEnum, String> DOCUMENT_TYPE_NAMES = new HashMap<>();
    private static final Map<Class<?>, TypeDocumentEnum> ENTITY_CLASS_MAP = new HashMap<>();

    static {
        // Initialize document type map
        DOCUMENT_TYPE_MAP.put(TypeDocumentEnum.LETTRE_BILLET, LettreSommationBillet.class);
        DOCUMENT_TYPE_MAP.put(TypeDocumentEnum.LETTRE_CARTE, LettreSommationCarte.class);
        DOCUMENT_TYPE_MAP.put(TypeDocumentEnum.RAPPORT_M, RapportM.class);

        // Initialize document type names
        DOCUMENT_TYPE_NAMES.put(TypeDocumentEnum.LETTRE_BILLET, "Lettre de Sommation (Billet)");
        DOCUMENT_TYPE_NAMES.put(TypeDocumentEnum.LETTRE_CARTE, "Lettre de Sommation (Carte)");
        DOCUMENT_TYPE_NAMES.put(TypeDocumentEnum.RAPPORT_M, "Rapport M");

        // Initialize entity class map
        ENTITY_CLASS_MAP.put(LettreSommationBillet.class, TypeDocumentEnum.LETTRE_BILLET);
        ENTITY_CLASS_MAP.put(LettreSommationCarte.class, TypeDocumentEnum.LETTRE_CARTE);
        ENTITY_CLASS_MAP.put(RapportM.class, TypeDocumentEnum.RAPPORT_M);
    }

    @Override
    public Class<?> getEntityClassForDocumentType(TypeDocumentEnum typeDocument) {
        return DOCUMENT_TYPE_MAP.get(typeDocument);
    }

    @Override
    public String getDocumentTypeName(TypeDocumentEnum typeDocument) {
        return DOCUMENT_TYPE_NAMES.get(typeDocument);
    }

    @Override
    public TypeDocumentEnum getDocumentTypeForEntityClass(Class<?> entityClass) {
        return ENTITY_CLASS_MAP.get(entityClass);
    }

    @Override
    public Map<TypeDocumentEnum, Class<?>> getAllDocumentTypes() {
        return new HashMap<>(DOCUMENT_TYPE_MAP);
    }
}