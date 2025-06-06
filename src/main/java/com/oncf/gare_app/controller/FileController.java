package com.oncf.gare_app.controller;

import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.exception.ResourceNotFoundException;
import com.oncf.gare_app.repository.PieceJointeRepository;
import com.oncf.gare_app.service.FileStorageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final PieceJointeRepository pieceJointeRepository;
    private final FileStorageService fileStorageService;

    /**
     * View file in browser (for PDFs, images, etc.)
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> viewFile(@PathVariable Long fileId, HttpServletResponse response) {
        try {
            PieceJointe pieceJointe = pieceJointeRepository.findById(fileId)
                    .orElseThrow(() -> new ResourceNotFoundException("Fichier non trouvé avec l'id: " + fileId));

            Path filePath = fileStorageService.getFilePath(pieceJointe.getCheminFichier());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new ResourceNotFoundException("Le fichier n'existe pas: " + pieceJointe.getCheminFichier());
            }

            // Determine content type
            String contentType = pieceJointe.getTypeMime();
            if (contentType == null) {
                try {
                    contentType = Files.probeContentType(filePath);
                } catch (IOException ex) {
                    contentType = "application/octet-stream";
                }
            }

            // Set headers to allow iframe viewing from Angular app
            response.setHeader("X-Frame-Options", "ALLOWALL"); // or remove this header entirely
            response.setHeader("Content-Security-Policy", "frame-ancestors http://localhost:4200 http://localhost:3000 'self'");

            // For development, you can also disable frame restrictions entirely:
            // response.setHeader("X-Frame-Options", "");
            // response.setHeader("Content-Security-Policy", "frame-ancestors *");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + pieceJointe.getNomFichier() + "\"")
                    .body(resource);

        } catch (MalformedURLException ex) {
            throw new RuntimeException("Erreur lors de la lecture du fichier: " + ex.getMessage());
        }
    }

    /**
     * Download file
     */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            PieceJointe pieceJointe = pieceJointeRepository.findById(fileId)
                    .orElseThrow(() -> new ResourceNotFoundException("Fichier non trouvé avec l'id: " + fileId));

            Path filePath = fileStorageService.getFilePath(pieceJointe.getCheminFichier());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new ResourceNotFoundException("Le fichier n'existe pas: " + pieceJointe.getCheminFichier());
            }

            // Force download with attachment header
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pieceJointe.getNomFichier() + "\"")
                    .body(resource);

        } catch (MalformedURLException ex) {
            throw new RuntimeException("Erreur lors du téléchargement du fichier: " + ex.getMessage());
        }
    }

    /**
     * Get file metadata
     */
    @GetMapping("/{fileId}/metadata")
    public ResponseEntity<PieceJointe> getFileMetadata(@PathVariable Long fileId) {
        PieceJointe pieceJointe = pieceJointeRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Fichier non trouvé avec l'id: " + fileId));

        return ResponseEntity.ok(pieceJointe);
    }

    /**
     * Check if file exists and is accessible
     */
    @RequestMapping(value = "/{fileId}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkFileAccess(@PathVariable Long fileId) {
        try {
            PieceJointe pieceJointe = pieceJointeRepository.findById(fileId)
                    .orElseThrow(() -> new ResourceNotFoundException("Fichier non trouvé avec l'id: " + fileId));

            Path filePath = fileStorageService.getFilePath(pieceJointe.getCheminFichier());

            if (Files.exists(filePath)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }
}