package com.oncf.gare_app.dto;

import com.oncf.gare_app.enums.TypeDocumentEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceJointeResponse {
    private Long id;
    private TypeDocumentEnum typeDocument;
    private Long documentId;
    private String nomFichier;
    private String cheminFichier;
    private String typeMime;
    private Long taille;
    private LocalDateTime dateUpload;
    private String downloadUrl;

    // Additional fields for UI display
    private String documentTypeName;
    private String documentTitle;
    private String fileSizeFormatted;
    private String dateUploadFormatted;
}