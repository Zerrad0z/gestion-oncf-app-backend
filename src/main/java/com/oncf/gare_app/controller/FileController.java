package com.oncf.gare_app.controller;

import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.exception.ResourceNotFoundException;
import com.oncf.gare_app.repository.PieceJointeRepository;
import com.oncf.gare_app.service.DocumentService;
import com.oncf.gare_app.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final PieceJointeRepository pieceJointeRepository;
    private final FileStorageService fileStorageService;
    private final DocumentService documentService;

    @GetMapping("/{pieceJointeId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long pieceJointeId) {
        try {
            PieceJointe pieceJointe = pieceJointeRepository.findById(pieceJointeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pièce jointe non trouvée avec l'id: " + pieceJointeId));

            Path filePath = fileStorageService.getFilePath(pieceJointe.getCheminFichier());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                // Try to determine file's content type
                String contentType = pieceJointe.getTypeMime();
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                // Get document info for better filename
                String documentTitle = documentService.getDocumentTitle(pieceJointe);
                String fileName = documentTitle != null
                        ? StringUtils.cleanPath(documentTitle) + "_" + pieceJointe.getNomFichier()
                        : pieceJointe.getNomFichier();

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                throw new ResourceNotFoundException("Le fichier n'existe pas: " + pieceJointe.getCheminFichier());
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Erreur: " + ex.getMessage());
        }
    }
}