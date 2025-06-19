package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.dto.BulkUpdateStatusRequest;
import com.oncf.gare_app.dto.LettreSommationCarteRequest;
import com.oncf.gare_app.dto.LettreSommationCarteResponse;
import com.oncf.gare_app.dto.PieceJointeResponse;
import com.oncf.gare_app.entity.LettreSommationCarte;
import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.StatutEnum;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.enums.TypeNotificationEnum;
import com.oncf.gare_app.enums.RoleUtilisateur;
import com.oncf.gare_app.exception.ResourceNotFoundException;
import com.oncf.gare_app.mapper.LettreSommationCarteMapper;
import com.oncf.gare_app.mapper.PieceJointeMapper;
import com.oncf.gare_app.repository.LettreSommationCarteRepository;
import com.oncf.gare_app.repository.PieceJointeRepository;
import com.oncf.gare_app.repository.UtilisateurSystemeRepository;
import com.oncf.gare_app.service.FileStorageService;
import com.oncf.gare_app.service.HistoriqueService;
import com.oncf.gare_app.service.LettreSommationCarteService;
import com.oncf.gare_app.service.NotificationService;
import com.oncf.gare_app.service.UtilisateurService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LettreSommationCarteServiceImpl implements LettreSommationCarteService {

    @PersistenceContext
    private EntityManager entityManager;

    private final LettreSommationCarteRepository lettreSommationCarteRepository;
    private final PieceJointeRepository pieceJointeRepository;
    private final UtilisateurSystemeRepository utilisateurSystemeRepository;
    private final LettreSommationCarteMapper lettreSommationCarteMapper;
    private final PieceJointeMapper pieceJointeMapper;
    private final FileStorageService fileStorageService;
    private final HistoriqueService historiqueService;
    private final NotificationService notificationService;
    private final UtilisateurService utilisateurService;

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationCarteResponse> getAllLettresSommationCarte() {
        List<LettreSommationCarteResponse> responses = lettreSommationCarteRepository.findAll().stream()
                .map(lettreSommationCarteMapper::toDto)
                .collect(Collectors.toList());

        // Load piece jointes for each lettre
        for (LettreSommationCarteResponse response : responses) {
            List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                    TypeDocumentEnum.LETTRE_CARTE, response.getId());

            List<PieceJointeResponse> pieceJointeResponses = piecesJointes.stream()
                    .map(pieceJointeMapper::toDto)
                    .collect(Collectors.toList());

            response.setPiecesJointes(pieceJointeResponses);
        }

        return responses;
    }

    @Transactional(readOnly = true)
    @Override
    public LettreSommationCarteResponse getLettreSommationCarteById(Long id) {
        LettreSommationCarte lettreSommationCarte = lettreSommationCarteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lettre de sommation carte non trouvée avec l'id: " + id));

        LettreSommationCarteResponse response = lettreSommationCarteMapper.toDto(lettreSommationCarte);

        // Load piece jointes
        List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                TypeDocumentEnum.LETTRE_CARTE, id);

        List<PieceJointeResponse> pieceJointeResponses = piecesJointes.stream()
                .map(pieceJointeMapper::toDto)
                .collect(Collectors.toList());

        response.setPiecesJointes(pieceJointeResponses);

        return response;
    }

    @Transactional
    @Override
    public LettreSommationCarteResponse createLettreSommationCarte(
            LettreSommationCarteRequest request, List<MultipartFile> fichiers) {

        // Validate permissions and get current user
        utilisateurService.validateCanCreate();
        UtilisateurSysteme currentUser = utilisateurService.getCurrentUser();

        // Check if a lettre with the same numeroCarte already exists
        if (lettreSommationCarteRepository.existsByNumeroCarte(request.getNumeroCarte())) {
            throw new RuntimeException("Une lettre de sommation carte avec le numéro " + request.getNumeroCarte() + " existe déjà");
        }

        // Create lettre sommation with current user
        LettreSommationCarte lettreSommationCarte = lettreSommationCarteMapper.toEntity(request);
        lettreSommationCarte.setUtilisateur(currentUser);

        // Save the entity FIRST to get the ID
        lettreSommationCarte = lettreSommationCarteRepository.save(lettreSommationCarte);

        // Force flush to ensure the entity is saved and has an ID
        entityManager.flush();

        // Log the saved entity for debugging
        System.out.println("Saved LettreSommationCarte with ID: " + lettreSommationCarte.getId() +
                " by user: " + currentUser.getNomUtilisateur());

        // Process file uploads if provided
        if (fichiers != null && !fichiers.isEmpty()) {
            try {
                for (MultipartFile fichier : fichiers) {
                    if (!fichier.isEmpty()) {
                        // Store file and get path
                        String fileName = fileStorageService.storeFile(fichier, "lettre-carte-" + lettreSommationCarte.getId());

                        // Create PieceJointe entity
                        PieceJointe pieceJointe = PieceJointe.builder()
                                .typeDocument(TypeDocumentEnum.LETTRE_CARTE)
                                .documentId(lettreSommationCarte.getId())
                                .nomFichier(fichier.getOriginalFilename())
                                .cheminFichier(fileName)
                                .typeMime(fichier.getContentType())
                                .taille(fichier.getSize())
                                .dateUpload(LocalDateTime.now())
                                .build();

                        System.out.println("About to save PieceJointe: " +
                                "TypeDocument: " + pieceJointe.getTypeDocument() +
                                ", DocumentId: " + pieceJointe.getDocumentId() +
                                ", FileName: " + pieceJointe.getNomFichier());

                        // Save PieceJointe to database
                        pieceJointe = pieceJointeRepository.save(pieceJointe);

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
                TypeDocumentEnum.LETTRE_CARTE,
                lettreSommationCarte.getId(),
                currentUser
        );

        // Notify supervisors about new document creation
        notifyDocumentCreation(lettreSommationCarte);

        // Return the DTO
        return lettreSommationCarteMapper.toDto(lettreSommationCarte);
    }

    @Transactional
    @Override
    public LettreSommationCarteResponse updateLettreSommationCarte(
            Long id, LettreSommationCarteRequest request, List<MultipartFile> fichiers) {

        // Validate permissions and get current user
        utilisateurService.validateCanUpdate();
        UtilisateurSysteme currentUser = utilisateurService.getCurrentUser();

        LettreSommationCarte lettreSommationCarte = lettreSommationCarteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lettre de sommation carte non trouvée avec l'id: " + id));

        utilisateurService.validateCanEditDocument(lettreSommationCarte.getUtilisateur());

        if (!lettreSommationCarte.getNumeroCarte().equals(request.getNumeroCarte()) &&
                lettreSommationCarteRepository.existsByNumeroCarte(request.getNumeroCarte())) {
            throw new RuntimeException("Une lettre de sommation carte avec le numéro " + request.getNumeroCarte() + " existe déjà");
        }

        // Store old status for history tracking
        String oldStatus = lettreSommationCarte.getStatut() != null ?
                lettreSommationCarte.getStatut().toString() : null;

        // Update entity fields
        lettreSommationCarteMapper.updateEntityFromDto(request, lettreSommationCarte);

        // Process file uploads if new files were provided
        if (fichiers != null && !fichiers.isEmpty()) {
            try {
                // Delete existing files from storage
                List<PieceJointe> existingPieces = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                        TypeDocumentEnum.LETTRE_CARTE, id);

                for (PieceJointe piece : existingPieces) {
                    fileStorageService.deleteFile(piece.getCheminFichier());
                    pieceJointeRepository.delete(piece);
                }

                // Add new pieces jointes
                for (MultipartFile fichier : fichiers) {
                    if (!fichier.isEmpty()) {
                        String fileName = fileStorageService.storeFile(fichier, "lettre-carte-" + id);

                        PieceJointe pieceJointe = PieceJointe.builder()
                                .typeDocument(TypeDocumentEnum.LETTRE_CARTE)
                                .documentId(id)
                                .nomFichier(fichier.getOriginalFilename())
                                .cheminFichier(fileName)
                                .typeMime(fichier.getContentType())
                                .taille(fichier.getSize())
                                .dateUpload(LocalDateTime.now())
                                .build();

                        pieceJointeRepository.save(pieceJointe);
                    }
                }

                // Track file uploads
                historiqueService.trackDocumentAction(
                        TypeDocumentEnum.LETTRE_CARTE,
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
        lettreSommationCarte = lettreSommationCarteRepository.save(lettreSommationCarte);

        // Track document update
        historiqueService.trackDocumentUpdate(
                TypeDocumentEnum.LETTRE_CARTE,
                id,
                currentUser,
                "Mise à jour générale"
        );

        // Track status change if applicable
        String newStatus = lettreSommationCarte.getStatut() != null ?
                lettreSommationCarte.getStatut().toString() : null;

        if (oldStatus != null && newStatus != null && !oldStatus.equals(newStatus)) {
            historiqueService.trackDocumentStatusChange(
                    TypeDocumentEnum.LETTRE_CARTE,
                    lettreSommationCarte.getId(),
                    currentUser,
                    oldStatus,
                    newStatus,
                    "Changement de statut"
            );

            // Notify relevant users about status change
            notifyStatusChange(lettreSommationCarte, oldStatus, newStatus);
        }

        // Map to response
        return lettreSommationCarteMapper.toDto(lettreSommationCarte);
    }

    @Transactional
    @Override
    public void deleteLettreSommationCarte(Long id) {
        // Validate permissions and get current user
        utilisateurService.validateCanDelete();
        UtilisateurSysteme currentUser = utilisateurService.getCurrentUser();

        LettreSommationCarte lettreSommationCarte = lettreSommationCarteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lettre de sommation carte non trouvée avec l'id: " + id));

        // Delete file attachments
        try {
            List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                    TypeDocumentEnum.LETTRE_CARTE, id);

            for (PieceJointe piece : piecesJointes) {
                fileStorageService.deleteFile(piece.getCheminFichier());
                pieceJointeRepository.delete(piece);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la suppression des fichiers", e);
        }

        // Track document deletion
        historiqueService.trackDocumentDeletion(
                TypeDocumentEnum.LETTRE_CARTE,
                id,
                currentUser
        );

        // Delete lettre sommation
        lettreSommationCarteRepository.deleteById(id);

        // Notify supervisors about deletion
        if (lettreSommationCarte.getAct() != null &&
                lettreSommationCarte.getAct().getAntenne() != null &&
                lettreSommationCarte.getAct().getAntenne().getSection() != null) {

            List<UtilisateurSysteme> supervisors = utilisateurSystemeRepository
                    .findByRoleAndAntenneSection(RoleUtilisateur.SUPERVISEUR,
                            lettreSommationCarte.getAct().getAntenne().getSection());

            for (UtilisateurSysteme supervisor : supervisors) {
                notificationService.createNotification(
                        supervisor,
                        TypeNotificationEnum.ALERTE,
                        "Une lettre de sommation (carte) a été supprimée par " + currentUser.getNomPrenom() +
                                " pour l'agent " + lettreSommationCarte.getAct().getNomPrenom() +
                                " (Matricule: " + lettreSommationCarte.getAct().getMatricule() + ")",
                        "/documents/lettre-carte"
                );
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationCarteResponse> searchLettresSommationCarte(
            Long actId, Long gareId, Long trainId, StatutEnum statut,
            String numeroCarte, String typeCarte, LocalDate dateDebut, LocalDate dateFin) {

        List<LettreSommationCarteResponse> responses = lettreSommationCarteRepository.search(
                        actId, gareId, trainId, statut, numeroCarte, typeCarte, dateDebut, dateFin)
                .stream()
                .map(lettreSommationCarteMapper::toDto)
                .collect(Collectors.toList());

        // Load piece jointes for each lettre
        for (LettreSommationCarteResponse response : responses) {
            List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                    TypeDocumentEnum.LETTRE_CARTE, response.getId());

            List<PieceJointeResponse> pieceJointeResponses = piecesJointes.stream()
                    .map(pieceJointeMapper::toDto)
                    .collect(Collectors.toList());

            response.setPiecesJointes(pieceJointeResponses);
        }

        return responses;
    }

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationCarteResponse> getLettreSommationCarteByActId(Long actId) {
        return lettreSommationCarteRepository.findByActId(actId).stream()
                .map(lettreSommationCarteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationCarteResponse> getLettreSommationCarteByGareId(Long gareId) {
        return lettreSommationCarteRepository.findByGareId(gareId).stream()
                .map(lettreSommationCarteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationCarteResponse> getLettreSommationCarteByTrainId(Long trainId) {
        return lettreSommationCarteRepository.findByTrainId(trainId).stream()
                .map(lettreSommationCarteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationCarteResponse> getLettreSommationCarteByStatut(StatutEnum statut) {
        return lettreSommationCarteRepository.findByStatut(statut).stream()
                .map(lettreSommationCarteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationCarteResponse> getLettreSommationCarteByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        return lettreSommationCarteRepository.findByDateInfractionBetween(dateDebut, dateFin).stream()
                .map(lettreSommationCarteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsLettreSommationCarteByNumeroCarte(String numeroCarte) {
        return lettreSommationCarteRepository.existsByNumeroCarte(numeroCarte);
    }

    @Transactional
    @Override
    public List<LettreSommationCarteResponse> updateBulkStatus(BulkUpdateStatusRequest request) {
        // Validate permissions and get current user
        utilisateurService.validateCanUpdateBulkStatus();
        UtilisateurSysteme currentUser = utilisateurService.getCurrentUser();

        List<LettreSommationCarte> lettres = lettreSommationCarteRepository.findAllById(request.getIds());

        if (lettres.size() != request.getIds().size()) {
            throw new ResourceNotFoundException("Une ou plusieurs lettres de sommation carte n'ont pas été trouvées");
        }

        for (LettreSommationCarte lettre : lettres) {
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
                    TypeDocumentEnum.LETTRE_CARTE,
                    lettre.getId(),
                    currentUser,
                    oldStatus,
                    request.getNewStatus().toString(),
                    "Changement de statut par lot: " + request.getCommentaire()
            );

            // Notify about status change
            notifyStatusChange(lettre, oldStatus, request.getNewStatus().toString());
        }

        lettres = lettreSommationCarteRepository.saveAll(lettres);

        // Map to responses
        return lettres.stream()
                .map(lettreSommationCarteMapper::toDto)
                .collect(Collectors.toList());
    }

    private void notifyDocumentCreation(LettreSommationCarte lettre) {
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
                        "Une nouvelle lettre de sommation (carte) a été créée par " +
                                lettre.getUtilisateur().getNomPrenom() + " pour l'agent " +
                                lettre.getAct().getNomPrenom() + " (Matricule: " +
                                lettre.getAct().getMatricule() + ")",
                        "/documents/lettre-carte/" + lettre.getId()
                );
            }
        }
    }

    private void notifyStatusChange(LettreSommationCarte lettre, String oldStatus, String newStatus) {
        // Determine notification type based on the new status
        TypeNotificationEnum notificationType = TypeNotificationEnum.INFO;
        if (newStatus.contains("NON_REGULARISEE") || newStatus.contains("REJETE")) {
            notificationType = TypeNotificationEnum.ALERTE;
        } else if (newStatus.contains("URGENT")) {
            notificationType = TypeNotificationEnum.URGENT;
        }

        // Notify the supervisors
        if (lettre.getAct() != null &&
                lettre.getAct().getAntenne() != null &&
                lettre.getAct().getAntenne().getSection() != null) {

            List<UtilisateurSysteme> supervisors = utilisateurSystemeRepository
                    .findByRoleAndAntenneSection(RoleUtilisateur.SUPERVISEUR,
                            lettre.getAct().getAntenne().getSection());

            for (UtilisateurSysteme supervisor : supervisors) {
                notificationService.createNotification(
                        supervisor,
                        notificationType,
                        "Une lettre de sommation (carte) a changé de statut de \"" +
                                oldStatus + "\" à \"" + newStatus + "\" pour l'agent " +
                                lettre.getAct().getNomPrenom() + " (Matricule: " +
                                lettre.getAct().getMatricule() + ")",
                        "/documents/lettre-carte/" + lettre.getId()
                );
            }
        }

        // Also notify the document creator (encadrant)
        if (lettre.getUtilisateur() != null) {
            notificationService.createNotification(
                    lettre.getUtilisateur(),
                    notificationType,
                    "Le statut d'une lettre de sommation (carte) a été modifié de \"" +
                            oldStatus + "\" à \"" + newStatus + "\"",
                    "/documents/lettre-carte/" + lettre.getId()
            );
        }
    }
}