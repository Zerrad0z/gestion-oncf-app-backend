package com.oncf.gare_app.controller;

import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.enums.TypeDocumentEnum;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/v1/downloads")
@RequiredArgsConstructor
public class FileDownloadController {

    private final PieceJointeRepository pieceJointeRepository;
    private final FileStorageService fileStorageService;
    private final DocumentService documentService;

    @GetMapping("/files/{pieceJointeId}")
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
                        ? documentTitle.replaceAll("[^a-zA-Z0-9.-]", "_") + "_" + pieceJointe.getNomFichier()
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

    @GetMapping("/documents/{typeDocument}/{documentId}")
    public ResponseEntity<StreamingResponseBody> downloadAllDocumentFiles(
            @PathVariable TypeDocumentEnum typeDocument,
            @PathVariable Long documentId) {

        // Get all pieces jointes for this document
        List<PieceJointe> piecesJointes = documentService.getPiecesJointesForDocument(typeDocument, documentId);

        if (piecesJointes.isEmpty()) {
            throw new ResourceNotFoundException("Aucune pièce jointe trouvée pour ce document");
        }

        // Get document title for zip filename
        Object document = documentService.getDocumentByTypeAndId(typeDocument, documentId);
        String documentTitle = "document_" + documentId;
        if (document != null) {
            documentTitle = documentService.getDocumentTitle(piecesJointes.get(0))
                    .replaceAll("[^a-zA-Z0-9.-]", "_");
        }

        // Set up response
        String zipFileName = documentTitle + "_files.zip";

        // Create a streaming response
        StreamingResponseBody responseBody = outputStream -> {
            try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
                for (PieceJointe pieceJointe : piecesJointes) {
                    Path filePath = fileStorageService.getFilePath(pieceJointe.getCheminFichier());

                    // Only add file if it exists
                    if (Files.exists(filePath)) {
                        ZipEntry zipEntry = new ZipEntry(pieceJointe.getNomFichier());
                        zipOut.putNextEntry(zipEntry);

                        try (InputStream inputStream = Files.newInputStream(filePath)) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                zipOut.write(buffer, 0, bytesRead);
                            }
                        }

                        zipOut.closeEntry();
                    }
                }

                zipOut.finish();
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la création du fichier ZIP", e);
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }

    @GetMapping("/type/{typeDocument}")
    public ResponseEntity<StreamingResponseBody> downloadAllDocumentTypeFiles(
            @PathVariable TypeDocumentEnum typeDocument) {

        // Get all pieces jointes for this document type
        List<PieceJointe> piecesJointes = pieceJointeRepository.findAllByTypeDocument(typeDocument);

        if (piecesJointes.isEmpty()) {
            throw new ResourceNotFoundException("Aucune pièce jointe trouvée pour ce type de document");
        }

        // Set up response
        String zipFileName = typeDocument.name().toLowerCase() + "_files.zip";

        // Create a streaming response
        StreamingResponseBody responseBody = outputStream -> {
            try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
                for (PieceJointe pieceJointe : piecesJointes) {
                    Path filePath = fileStorageService.getFilePath(pieceJointe.getCheminFichier());

                    // Only add file if it exists
                    if (Files.exists(filePath)) {
                        // Create a more descriptive filename including document info
                        String documentInfo = documentService.getDocumentTitle(pieceJointe);
                        String entryName = (documentInfo != null ?
                                documentInfo.replaceAll("[^a-zA-Z0-9.-]", "_") + "/" : "") +
                                pieceJointe.getNomFichier();

                        ZipEntry zipEntry = new ZipEntry(entryName);
                        zipOut.putNextEntry(zipEntry);

                        try (InputStream inputStream = Files.newInputStream(filePath)) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                zipOut.write(buffer, 0, bytesRead);
                            }
                        }

                        zipOut.closeEntry();
                    }
                }

                zipOut.finish();
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la création du fichier ZIP", e);
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }
}