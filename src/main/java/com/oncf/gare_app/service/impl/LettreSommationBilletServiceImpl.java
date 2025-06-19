package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.dto.BulkUpdateStatusRequest;
import com.oncf.gare_app.dto.LettreSommationBilletRequest;
import com.oncf.gare_app.dto.LettreSommationBilletResponse;
import com.oncf.gare_app.dto.PieceJointeResponse;
import com.oncf.gare_app.entity.LettreSommationBillet;
import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.RoleUtilisateur;
import com.oncf.gare_app.enums.StatutEnum;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.enums.TypeNotificationEnum;
import com.oncf.gare_app.exception.ResourceNotFoundException;
import com.oncf.gare_app.mapper.LettreSommationBilletMapper;
import com.oncf.gare_app.mapper.PieceJointeMapper;
import com.oncf.gare_app.repository.LettreSommationBilletRepository;
import com.oncf.gare_app.repository.PieceJointeRepository;
import com.oncf.gare_app.repository.UtilisateurSystemeRepository;
import com.oncf.gare_app.service.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LettreSommationBilletServiceImpl implements LettreSommationBilletService {

    @PersistenceContext
    private EntityManager entityManager;

    private final LettreSommationBilletRepository lettreSommationBilletRepository;
    private final PieceJointeRepository pieceJointeRepository;
    private final LettreSommationBilletMapper lettreSommationBilletMapper;
    private final PieceJointeMapper pieceJointeMapper;
    private final FileStorageService fileStorageService;
    private final HistoriqueService historiqueService;
    private final NotificationService notificationService;
    private final UtilisateurSystemeRepository utilisateurSystemeRepository;
    private final FileValidationService fileValidationService;
    private final UtilisateurService utilisateurService; // Added

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationBilletResponse> getAllLettresSommationBillet() {
        List<LettreSommationBilletResponse> responses = lettreSommationBilletRepository.findAll().stream()
                .map(lettreSommationBilletMapper::toDto)
                .collect(Collectors.toList());

        // Load piece jointes for each lettre
        for (LettreSommationBilletResponse response : responses) {
            List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                    TypeDocumentEnum.LETTRE_BILLET, response.getId());

            List<PieceJointeResponse> pieceJointeResponses = piecesJointes.stream()
                    .map(pieceJointeMapper::toDto)
                    .collect(Collectors.toList());

            response.setPiecesJointes(pieceJointeResponses);
        }

        return responses;
    }

    @Transactional(readOnly = true)
    @Override
    public LettreSommationBilletResponse getLettreSommationBilletById(Long id) {
        LettreSommationBillet lettreSommationBillet = lettreSommationBilletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lettre de sommation billet non trouvée avec l'id: " + id));

        LettreSommationBilletResponse response = lettreSommationBilletMapper.toDto(lettreSommationBillet);

        // Load piece jointes
        List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                TypeDocumentEnum.LETTRE_BILLET, id);

        List<PieceJointeResponse> pieceJointeResponses = piecesJointes.stream()
                .map(pieceJointeMapper::toDto)
                .collect(Collectors.toList());

        response.setPiecesJointes(pieceJointeResponses);

        return response;
    }

    @Transactional
    @Override
    public LettreSommationBilletResponse createLettreSommationBillet(
            LettreSommationBilletRequest request, List<MultipartFile> fichiers) {

        // Validate permissions and get current user
        utilisateurService.validateCanCreate();
        UtilisateurSysteme currentUser = utilisateurService.getCurrentUser();

        // Check if a lettre with the same numeroBillet already exists
        if (lettreSommationBilletRepository.existsByNumeroBillet(request.getNumeroBillet())) {
            throw new RuntimeException("Une lettre de sommation billet avec le numéro " + request.getNumeroBillet() + " existe déjà");
        }

        // Create lettre sommation with current user
        LettreSommationBillet lettreSommationBillet = lettreSommationBilletMapper.toEntity(request);
        lettreSommationBillet.setUtilisateur(currentUser);

        // Save the entity FIRST to get the ID
        lettreSommationBillet = lettreSommationBilletRepository.save(lettreSommationBillet);

        // Force flush to ensure the entity is saved and has an ID
        entityManager.flush();

        // Log the saved entity for debugging
        System.out.println("Saved LettreSommationBillet with ID: " + lettreSommationBillet.getId() +
                " by user: " + currentUser.getNomUtilisateur());

        // Process file uploads if provided
        if (fichiers != null && !fichiers.isEmpty()) {
            try {
                for (MultipartFile fichier : fichiers) {
                    if (!fichier.isEmpty()) {
                        // Validate file first
                        String validationError = fileValidationService.getValidationErrorMessage(fichier);
                        if (validationError != null) {
                            throw new RuntimeException("Validation error for file " + fichier.getOriginalFilename() + ": " + validationError);
                        }

                        // Store file and get path
                        String fileName = fileStorageService.storeFile(fichier, "lettre-billet-" + lettreSommationBillet.getId());

                        // Create PieceJointe entity
                        PieceJointe pieceJointe = PieceJointe.builder()
                                .typeDocument(TypeDocumentEnum.LETTRE_BILLET)
                                .documentId(lettreSommationBillet.getId())
                                .nomFichier(fichier.getOriginalFilename())
                                .cheminFichier(fileName)
                                .typeMime(fichier.getContentType())
                                .taille(fichier.getSize())
                                .dateUpload(LocalDateTime.now())
                                .build();

                        // Log before saving for debugging
                        System.out.println("About to save PieceJointe: " +
                                "TypeDocument: " + pieceJointe.getTypeDocument() +
                                ", DocumentId: " + pieceJointe.getDocumentId() +
                                ", FileName: " + pieceJointe.getNomFichier());

                        // Save PieceJointe to database
                        pieceJointe = pieceJointeRepository.save(pieceJointe);

                        // Log after saving for debugging
                        System.out.println("Successfully saved PieceJointe with ID: " + pieceJointe.getId());
                    }
                }

            } catch (Exception e) {
                System.err.println("Error during file processing: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error storing files: " + e.getMessage(), e);
            }
        }

        // Track document creation
        historiqueService.trackDocumentCreation(
                TypeDocumentEnum.LETTRE_BILLET,
                lettreSommationBillet.getId(),
                currentUser
        );

        // Notify supervisors about new document creation
        notifyDocumentCreation(lettreSommationBillet);

        // Return the DTO
        return lettreSommationBilletMapper.toDto(lettreSommationBillet);
    }

    @Transactional
    @Override
    public LettreSommationBilletResponse updateLettreSommationBillet(
            Long id, LettreSommationBilletRequest request, List<MultipartFile> fichiers) {

        // Validate permissions and get current user
        utilisateurService.validateCanUpdate();
        UtilisateurSysteme currentUser = utilisateurService.getCurrentUser();

        LettreSommationBillet lettreSommationBillet = lettreSommationBilletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lettre de sommation billet non trouvée avec l'id: " + id));

        utilisateurService.validateCanEditDocument(lettreSommationBillet.getUtilisateur());

        if (!lettreSommationBillet.getNumeroBillet().equals(request.getNumeroBillet()) &&
                lettreSommationBilletRepository.existsByNumeroBillet(request.getNumeroBillet())) {
            throw new RuntimeException("Une lettre de sommation billet avec le numéro " + request.getNumeroBillet() + " existe déjà");
        }

        // Store old status for history tracking
        String oldStatus = lettreSommationBillet.getStatut() != null ?
                lettreSommationBillet.getStatut().toString() : null;

        // Update entity fields
        lettreSommationBilletMapper.updateEntityFromDto(request, lettreSommationBillet);

        // Process file uploads if new files were provided
        if (fichiers != null && !fichiers.isEmpty()) {
            try {
                // Get existing pieces jointes
                List<PieceJointe> existingPieces = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                        TypeDocumentEnum.LETTRE_BILLET, id);

                // Delete existing files from storage and database
                for (PieceJointe piece : existingPieces) {
                    try {
                        fileStorageService.deleteFile(piece.getCheminFichier());
                    } catch (IOException e) {
                        System.err.println("Could not delete file: " + piece.getCheminFichier());
                    }
                    pieceJointeRepository.delete(piece);
                }

                // Add new pieces jointes
                List<PieceJointe> newPiecesJointes = new ArrayList<>();

                for (MultipartFile fichier : fichiers) {
                    if (!fichier.isEmpty()) {
                        // Validate file
                        String validationError = fileValidationService.getValidationErrorMessage(fichier);
                        if (validationError != null) {
                            throw new RuntimeException("Validation error for file " + fichier.getOriginalFilename() + ": " + validationError);
                        }

                        String fileName = fileStorageService.storeFile(fichier, "lettre-billet-" + id);

                        PieceJointe pieceJointe = PieceJointe.builder()
                                .typeDocument(TypeDocumentEnum.LETTRE_BILLET)
                                .documentId(id)
                                .nomFichier(fichier.getOriginalFilename())
                                .cheminFichier(fileName)
                                .typeMime(fichier.getContentType())
                                .taille(fichier.getSize())
                                .dateUpload(LocalDateTime.now())
                                .build();

                        // Save to database
                        pieceJointe = pieceJointeRepository.save(pieceJointe);
                        newPiecesJointes.add(pieceJointe);
                    }
                }

                // Update the relationship
                lettreSommationBillet.setPiecesJointes(newPiecesJointes);

                // Track file uploads
                historiqueService.trackDocumentAction(
                        TypeDocumentEnum.LETTRE_BILLET,
                        id,
                        currentUser,
                        "MISE_A_JOUR_FICHIERS",
                        "Nouveaux fichiers ajoutés: " + fichiers.size(),
                        null,
                        null
                );

            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la mise à jour des fichiers", e);
            }
        }

        // Save the updated entity
        lettreSommationBillet = lettreSommationBilletRepository.save(lettreSommationBillet);

        // Track document update
        historiqueService.trackDocumentUpdate(
                TypeDocumentEnum.LETTRE_BILLET,
                id,
                currentUser,
                "Mise à jour générale"
        );

        // Track status change if applicable
        String newStatus = lettreSommationBillet.getStatut() != null ?
                lettreSommationBillet.getStatut().toString() : null;

        if (oldStatus != null && newStatus != null && !oldStatus.equals(newStatus)) {
            historiqueService.trackDocumentStatusChange(
                    TypeDocumentEnum.LETTRE_BILLET,
                    lettreSommationBillet.getId(),
                    currentUser,
                    oldStatus,
                    newStatus,
                    "Changement de statut"
            );

            // Notify relevant users about status change
            notifyStatusChange(lettreSommationBillet, oldStatus, newStatus);
        }

        return lettreSommationBilletMapper.toDto(lettreSommationBillet);
    }

    @Transactional
    @Override
    public void deleteLettreSommationBillet(Long id) {
        // Validate permissions and get current user
        utilisateurService.validateCanDelete();
        UtilisateurSysteme currentUser = utilisateurService.getCurrentUser();

        LettreSommationBillet lettreSommationBillet = lettreSommationBilletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lettre de sommation billet non trouvée avec l'id: " + id));

        // Delete file attachments
        try {
            List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                    TypeDocumentEnum.LETTRE_BILLET, id);

            for (PieceJointe piece : piecesJointes) {
                fileStorageService.deleteFile(piece.getCheminFichier());
                pieceJointeRepository.delete(piece);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la suppression des fichiers", e);
        }

        // Track document deletion
        historiqueService.trackDocumentDeletion(
                TypeDocumentEnum.LETTRE_BILLET,
                id,
                currentUser
        );

        // Delete lettre sommation
        lettreSommationBilletRepository.deleteById(id);

        // Notify supervisors about deletion
        if (lettreSommationBillet.getAct() != null &&
                lettreSommationBillet.getAct().getAntenne() != null &&
                lettreSommationBillet.getAct().getAntenne().getSection() != null) {

            List<UtilisateurSysteme> supervisors = utilisateurSystemeRepository
                    .findByRoleAndAntenneSection(RoleUtilisateur.SUPERVISEUR,
                            lettreSommationBillet.getAct().getAntenne().getSection());

            for (UtilisateurSysteme supervisor : supervisors) {
                notificationService.createNotification(
                        supervisor,
                        TypeNotificationEnum.ALERTE,
                        "Une lettre de sommation (billet) a été supprimée par " + currentUser.getNomPrenom() +
                                " pour l'agent " + lettreSommationBillet.getAct().getNomPrenom() +
                                " (Matricule: " + lettreSommationBillet.getAct().getMatricule() + ")",
                        "/documents/lettre-billet"
                );
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationBilletResponse> searchLettresSommationBillet(
            Long actId, Long gareId, Long trainId, StatutEnum statut,
            String numeroBillet, LocalDate dateDebut, LocalDate dateFin) {

        List<LettreSommationBilletResponse> responses = lettreSommationBilletRepository.search(
                        actId, gareId, trainId, statut, numeroBillet, dateDebut, dateFin)
                .stream()
                .map(lettreSommationBilletMapper::toDto)
                .collect(Collectors.toList());

        // Load piece jointes for each lettre
        for (LettreSommationBilletResponse response : responses) {
            List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                    TypeDocumentEnum.LETTRE_BILLET, response.getId());

            List<PieceJointeResponse> pieceJointeResponses = piecesJointes.stream()
                    .map(pieceJointeMapper::toDto)
                    .collect(Collectors.toList());

            response.setPiecesJointes(pieceJointeResponses);
        }

        return responses;
    }

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationBilletResponse> getLettreSommationBilletByActId(Long actId) {
        return lettreSommationBilletRepository.findByActId(actId).stream()
                .map(lettreSommationBilletMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationBilletResponse> getLettreSommationBilletByGareId(Long gareId) {
        return lettreSommationBilletRepository.findByGareId(gareId).stream()
                .map(lettreSommationBilletMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationBilletResponse> getLettreSommationBilletByTrainId(Long trainId) {
        return lettreSommationBilletRepository.findByTrainId(trainId).stream()
                .map(lettreSommationBilletMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationBilletResponse> getLettreSommationBilletByStatut(StatutEnum statut) {
        return lettreSommationBilletRepository.findByStatut(statut).stream()
                .map(lettreSommationBilletMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationBilletResponse> getLettreSommationBilletByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        return lettreSommationBilletRepository.findByDateInfractionBetween(dateDebut, dateFin).stream()
                .map(lettreSommationBilletMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsLettreSommationBilletByNumeroBillet(String numeroBillet) {
        return lettreSommationBilletRepository.existsByNumeroBillet(numeroBillet);
    }

    @Transactional
    @Override
    public List<LettreSommationBilletResponse> updateBulkStatus(BulkUpdateStatusRequest request) {
        // Validate permissions and get current user
        utilisateurService.validateCanUpdateBulkStatus();
        UtilisateurSysteme currentUser = utilisateurService.getCurrentUser();

        List<LettreSommationBillet> lettres = lettreSommationBilletRepository.findAllById(request.getIds());

        if (lettres.size() != request.getIds().size()) {
            throw new ResourceNotFoundException("Une ou plusieurs lettres de sommation billet n'ont pas été trouvées");
        }

        for (LettreSommationBillet lettre : lettres) {
            // Store old status
            String oldStatus = lettre.getStatut() != null ? lettre.getStatut().toString() : null;

            // Update status
            lettre.setStatut(request.getNewStatus());

            // Add new comment if provided
            if (request.getCommentaire() != null && !request.getCommentaire().isEmpty()) {
                String existingComments = lettre.getCommentaires() != null ? lettre.getCommentaires() : "";
                String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String newComment = dateStr + " - Changement de statut à " + request.getNewStatus() +
                        " par " + currentUser.getNomPrenom() + ": " + request.getCommentaire();

                if (!existingComments.isEmpty()) {
                    existingComments += "\n\n";
                }

                lettre.setCommentaires(existingComments + newComment);
            }

            // Set treatment date if status indicates treatment is completed
            if (request.getNewStatus() == StatutEnum.REGULARISEE ||
                    request.getNewStatus() == StatutEnum.TRAITE ||
                    request.getNewStatus() == StatutEnum.REJETE) {
                lettre.setDateTraitement(LocalDate.now());
            }

            // Track status change
            historiqueService.trackDocumentStatusChange(
                    TypeDocumentEnum.LETTRE_BILLET,
                    lettre.getId(),
                    currentUser,
                    oldStatus,
                    request.getNewStatus().toString(),
                    "Changement de statut par lot: " + request.getCommentaire()
            );

            // Notify about status change
            notifyStatusChange(lettre, oldStatus, request.getNewStatus().toString());
        }

        lettres = lettreSommationBilletRepository.saveAll(lettres);

        // Map to responses
        List<LettreSommationBilletResponse> responses = lettres.stream()
                .map(lettreSommationBilletMapper::toDto)
                .collect(Collectors.toList());

        return responses;
    }

    private void notifyDocumentCreation(LettreSommationBillet lettre) {
        if (lettre.getAct() != null &&
                lettre.getAct().getAntenne() != null &&
                lettre.getAct().getAntenne().getSection() != null) {

            List<UtilisateurSysteme> supervisors = utilisateurSystemeRepository
                    .findByRoleAndAntenneSection(RoleUtilisateur.SUPERVISEUR,
                            lettre.getAct().getAntenne().getSection());

            for (UtilisateurSysteme supervisor : supervisors) {
                notificationService.createNotification(
                        supervisor,
                        TypeNotificationEnum.INFO,
                        "Une nouvelle lettre de sommation (billet) a été créée par " +
                                lettre.getUtilisateur().getNomPrenom() + " pour l'agent " +
                                lettre.getAct().getNomPrenom() + " (Matricule: " +
                                lettre.getAct().getMatricule() + ")",
                        "/documents/lettre-billet/" + lettre.getId()
                );
            }
        }
    }

    private void notifyStatusChange(LettreSommationBillet lettre, String oldStatus, String newStatus) {
        // Determine notification type based on new status
        TypeNotificationEnum notificationType = TypeNotificationEnum.INFO;
        if (newStatus.contains("NON_REGULARISEE") || newStatus.contains("REJETE")) {
            notificationType = TypeNotificationEnum.ALERTE;
        } else if (newStatus.contains("URGENT")) {
            notificationType = TypeNotificationEnum.URGENT;
        }

        // Notify the ACT agent's supervisor
        if (lettre.getAct() != null &&
                lettre.getAct().getAntenne() != null &&
                lettre.getAct().getAntenne().getSection() != null) {

            // Find supervisors for the section
            List<UtilisateurSysteme> supervisors = utilisateurSystemeRepository
                    .findByRoleAndAntenneSection(RoleUtilisateur.SUPERVISEUR,
                            lettre.getAct().getAntenne().getSection());

            for (UtilisateurSysteme supervisor : supervisors) {
                notificationService.createNotification(
                        supervisor,
                        notificationType,
                        "Une lettre de sommation (billet) a changé de statut de \"" +
                                oldStatus + "\" à \"" + newStatus + "\" pour l'agent " +
                                lettre.getAct().getNomPrenom() + " (Matricule: " +
                                lettre.getAct().getMatricule() + ")",
                        "/documents/lettre-billet/" + lettre.getId()
                );
            }
        }

        if (lettre.getUtilisateur() != null) {
            notificationService.createNotification(
                    lettre.getUtilisateur(),
                    notificationType,
                    "Le statut d'une lettre de sommation (billet) a été modifié de \"" +
                            oldStatus + "\" à \"" + newStatus + "\"",
                    "/documents/lettre-billet/" + lettre.getId()
            );
        }
    }
}