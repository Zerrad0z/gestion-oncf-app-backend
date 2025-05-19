package com.oncf.gare_app.mapper;

import com.oncf.gare_app.dto.PieceJointeResponse;
import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.service.DocumentService;
import com.oncf.gare_app.service.DocumentTypeResolver;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public abstract class PieceJointeMapper {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentTypeResolver documentTypeResolver;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Mapping(target = "downloadUrl", ignore = true)
    @Mapping(target = "documentTypeName", ignore = true)
    @Mapping(target = "documentTitle", ignore = true)
    @Mapping(target = "fileSizeFormatted", ignore = true)
    @Mapping(target = "dateUploadFormatted", ignore = true)
    public abstract PieceJointeResponse toDto(PieceJointe entity);

    @AfterMapping
    protected void setAdditionalFields(@MappingTarget PieceJointeResponse response, PieceJointe entity) {
        // Set download URL
        response.setDownloadUrl(baseUrl + "/api/v1/files/" + entity.getId());

        // Set document type name
        response.setDocumentTypeName(documentTypeResolver.getDocumentTypeName(entity.getTypeDocument()));

        // Set document title
        response.setDocumentTitle(documentService.getDocumentTitle(entity));

        // Format file size
        response.setFileSizeFormatted(formatFileSize(entity.getTaille()));

        // Format date
        if (entity.getDateUpload() != null) {
            response.setDateUploadFormatted(entity.getDateUpload().format(DATE_FORMATTER));
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}