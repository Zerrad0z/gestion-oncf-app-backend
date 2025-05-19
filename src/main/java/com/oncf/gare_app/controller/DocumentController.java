package com.oncf.gare_app.controller;

import com.oncf.gare_app.dto.PieceJointeResponse;
import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.exception.ResourceNotFoundException;
import com.oncf.gare_app.mapper.PieceJointeMapper;
import com.oncf.gare_app.service.DocumentService;
import com.oncf.gare_app.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final FileStorageService fileStorageService;
    private final PieceJointeMapper pieceJointeMapper;

    @GetMapping("/{typeDocument}/{documentId}/pieces-jointes")
    public ResponseEntity<List<PieceJointeResponse>> getPiecesJointesForDocument(
            @PathVariable TypeDocumentEnum typeDocument,
            @PathVariable Long documentId) {

        // Check if document exists
        if (!documentService.documentExists(typeDocument, documentId)) {
            throw new ResourceNotFoundException("Document non trouvé: " + typeDocument + " avec ID: " + documentId);
        }

        // Get pieces jointes
        List<PieceJointe> piecesJointes = documentService.getPiecesJointesForDocument(typeDocument, documentId);

        // Map to DTOs
        List<PieceJointeResponse> response = piecesJointes.stream()
                .map(pieceJointeMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{typeDocument}/{documentId}/pieces-jointes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<PieceJointeResponse>> addPiecesJointesToDocument(
            @PathVariable TypeDocumentEnum typeDocument,
            @PathVariable Long documentId,
            @RequestParam("files") List<MultipartFile> files) throws IOException {

        // Check if document exists
        if (!documentService.documentExists(typeDocument, documentId)) {
            throw new ResourceNotFoundException("Document non trouvé: " + typeDocument + " avec ID: " + documentId);
        }

        // Process files
        List<PieceJointe> piecesJointes = fileStorageService.createPiecesJointes(files, typeDocument, documentId);

        // Add to document
        for (PieceJointe pieceJointe : piecesJointes) {
            documentService.addPieceJointeToDocument(pieceJointe, typeDocument, documentId);
        }

        // Map to DTOs
        List<PieceJointeResponse> response = piecesJointes.stream()
                .map(pieceJointeMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{typeDocument}/{documentId}/pieces-jointes")
    public ResponseEntity<Void> removePiecesJointesFromDocument(
            @PathVariable TypeDocumentEnum typeDocument,
            @PathVariable Long documentId) {

        // Check if document exists
        if (!documentService.documentExists(typeDocument, documentId)) {
            throw new ResourceNotFoundException("Document non trouvé: " + typeDocument + " avec ID: " + documentId);
        }

        // Get pieces jointes to delete files
        List<PieceJointe> piecesJointes = documentService.getPiecesJointesForDocument(typeDocument, documentId);

        // Delete files
        for (PieceJointe pieceJointe : piecesJointes) {
            try {
                fileStorageService.deleteFile(pieceJointe.getCheminFichier());
            } catch (IOException e) {
                // Log error but continue
                System.err.println("Erreur lors de la suppression du fichier: " + pieceJointe.getCheminFichier());
            }
        }

        // Remove from document
        documentService.removePiecesJointesFromDocument(typeDocument, documentId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/pieces-jointes/{pieceJointeId}")
    public ResponseEntity<Void> removePieceJointe(@PathVariable Long pieceJointeId) {
        // Get the piece jointe
        PieceJointe pieceJointe = documentService.getPiecesJointesForDocument(null, null)
                .stream()
                .filter(p -> p.getId().equals(pieceJointeId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Pièce jointe non trouvée avec l'id: " + pieceJointeId));

        // Delete the file
        try {
            fileStorageService.deleteFile(pieceJointe.getCheminFichier());
        } catch (IOException e) {
            // Log error but continue
            System.err.println("Erreur lors de la suppression du fichier: " + pieceJointe.getCheminFichier());
        }

        // Remove the piece jointe
        documentService.removePiecesJointesFromDocument(pieceJointe.getTypeDocument(), pieceJointe.getDocumentId());

        return ResponseEntity.noContent().build();
    }
}