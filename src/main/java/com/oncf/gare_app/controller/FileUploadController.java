//package com.oncf.gare_app.controller;
//
//import com.oncf.gare_app.dto.ApiResponse;
//import com.oncf.gare_app.dto.PieceJointeResponse;
//import com.oncf.gare_app.entity.PieceJointe;
//import com.oncf.gare_app.enums.TypeDocumentEnum;
//import com.oncf.gare_app.exception.FileStorageException;
//import com.oncf.gare_app.exception.ResourceNotFoundException;
//import com.oncf.gare_app.mapper.PieceJointeMapper;
//import com.oncf.gare_app.service.DocumentService;
//import com.oncf.gare_app.service.FileStorageService;
//import com.oncf.gare_app.service.FileValidationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/v1/uploads")
//@RequiredArgsConstructor
//public class FileUploadController {
//
//    private final FileStorageService fileStorageService;
//    private final FileValidationService fileValidationService;
//    private final DocumentService documentService;
//    private final PieceJointeMapper pieceJointeMapper;
//
//    @PostMapping(value = "/{typeDocument}/{documentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> uploadFilesToDocument(
//            @PathVariable TypeDocumentEnum typeDocument,
//            @PathVariable Long documentId,
//            @RequestParam("files") List<MultipartFile> files) {
//
//        // Check if document exists
//        if (!documentService.documentExists(typeDocument, documentId)) {
//            throw new ResourceNotFoundException(
//                    "Document non trouvé: " + typeDocument + " avec ID: " + documentId);
//        }
//
//        // Validate files
//        List<String> errors = new ArrayList<>();
//        for (MultipartFile file : files) {
//            if (!file.isEmpty()) {
//                String errorMessage = fileValidationService.getValidationErrorMessage(file);
//                if (errorMessage != null) {
//                    errors.add(file.getOriginalFilename() + ": " + errorMessage);
//                }
//            }
//        }
//
//        // If there are validation errors, return them
//        if (!errors.isEmpty()) {
//            return ResponseEntity.badRequest().body(
//                    new ApiResponse(false, "Erreurs de validation des fichiers", errors));
//        }
//
//        try {
//            // Create and save pieces jointes
//            List<PieceJointe> piecesJointes = fileStorageService.createPiecesJointes(
//                    files, typeDocument, documentId);
//
//            // Map to responses
//            List<PieceJointeResponse> responses = piecesJointes.stream()
//                    .map(pieceJointeMapper::toDto)
//                    .collect(Collectors.toList());
//
//            return ResponseEntity.ok(new ApiResponse(
//                    true, "Fichiers téléchargés avec succès", responses));
//
//        } catch (IOException e) {
//            throw new FileStorageException("Erreur lors du téléchargement des fichiers", e);
//        }
//    }
//
//    @DeleteMapping("/{pieceJointeId}")
//    public ResponseEntity<?> deleteFile(@PathVariable Long pieceJointeId) {
//        // Get the piece jointe
//        List<PieceJointe> allPieces = documentService.getPiecesJointesForDocument(null, null);
//        PieceJointe pieceJointe = allPieces.stream()
//                .filter(p -> p.getId().equals(pieceJointeId))
//                .findFirst()
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Pièce jointe non trouvée avec l'id: " + pieceJointeId));
//
//        try {
//            // Delete the file
//            fileStorageService.deleteFile(pieceJointe.getCheminFichier());
//
//            // Remove from database
//            documentService.removePiecesJointesFromDocument(
//                    pieceJointe.getTypeDocument(), pieceJointe.getDocumentId());
//
//            return ResponseEntity.ok(new ApiResponse(true, "Fichier supprimé avec succès", null));
//
//        } catch (IOException e) {
//            throw new FileStorageException("Erreur lors de la suppression du fichier", e);
//        }
//    }
//
//    @GetMapping("/validate")
//    public ResponseEntity<?> validateFileType(
//            @RequestParam("fileName") String fileName,
//            @RequestParam("fileSize") long fileSize,
//            @RequestParam("mimeType") String mimeType) {
//
//        boolean isExtensionValid = fileValidationService.isAllowedExtension(fileName);
//        boolean isMimeTypeValid = fileValidationService.isAllowedMimeType(mimeType);
//        boolean isSizeValid = fileValidationService.isAllowedSize(fileSize);
//
//        if (isExtensionValid && isMimeTypeValid && isSizeValid) {
//            return ResponseEntity.ok(new ApiResponse(true, "Le fichier est valide", null));
//        } else {
//            List<String> errors = new ArrayList<>();
//            if (!isExtensionValid) {
//                errors.add("L'extension du fichier n'est pas autorisée");
//            }
//            if (!isMimeTypeValid) {
//                errors.add("Le type MIME du fichier n'est pas autorisé");
//            }
//            if (!isSizeValid) {
//                errors.add("La taille du fichier dépasse la limite autorisée");
//            }
//            return ResponseEntity.badRequest().body(
//                    new ApiResponse(false, "Le fichier n'est pas valide", errors));
//        }
//    }
//}