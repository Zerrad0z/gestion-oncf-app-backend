package com.oncf.gare_app.service;

import com.oncf.gare_app.enums.TypeDocumentEnum;

import java.util.Map;

public interface DocumentTypeResolver {

    /**
     * Get the entity class for a document type
     */
    Class<?> getEntityClassForDocumentType(TypeDocumentEnum typeDocument);

    /**
     * Get a human-readable name for a document type
     */
    String getDocumentTypeName(TypeDocumentEnum typeDocument);

    /**
     * Get the document type for an entity class
     */
    TypeDocumentEnum getDocumentTypeForEntityClass(Class<?> entityClass);

    /**
     * Get a map of all document types and their entity classes
     */
    Map<TypeDocumentEnum, Class<?>> getAllDocumentTypes();
}