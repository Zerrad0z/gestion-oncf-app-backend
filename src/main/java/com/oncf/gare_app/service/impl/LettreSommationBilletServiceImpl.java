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
import com.oncf.gare_app.service.FileStorageService;
import com.oncf.gare_app.service.HistoriqueService;
import com.oncf.gare_app.service.LettreSommationBilletService;
import com.oncf.gare_app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LettreSommationBilletServiceImpl implements LettreSommationBilletService {

    private final LettreSommationBilletRepository lettreSommationBilletRepository;
    private final PieceJointeRepository pieceJointeRepository;
    private final LettreSommationBilletMapper lettreSommationBilletMapper;
    private final PieceJointeMapper pieceJointeMapper;
    private final FileStorageService fileStorageService;
    private final HistoriqueService historiqueService;
    private final NotificationService notificationService;
    private final UtilisateurSystemeRepository utilisateurSystemeRepository;

    @Transactional(readOnly = true)
    @Override
    public List<LettreSommationBilletResponse> getAllLettresSommationBillet() {
        List<LettreSommationBilletResponse> responses = lettreSommationBilletRepository.findAll().stream()
                .map(lettreSommationBilletMapper::toDto)
                .collect(Collectors.toList());

        return responses;
    }

    @Transactional(readOnly = true)
    @Override
    public LettreSommationBilletResponse getLettreSommationBilletById(Long id) {
        LettreSommationBillet lettreSommationBillet = lettreSommationBilletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lettre de sommation billet non trouvée avec l'id: " + id));

        LettreSommationBilletResponse response = lettreSommationBilletMapper.toDto(lettreSommationBillet);

        return response;
    }

    // In LettreSommationBilletServiceImpl when creating a new document
    @Transactional
    @Override
    public LettreSommationBilletResponse createLettreSommationBillet(
            LettreSommationBilletRequest request, List<MultipartFile> fichiers) {

        // Get current user (encadrant)
        UtilisateurSysteme currentUser = (UtilisateurSysteme) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        // Create lettre sommation
        LettreSommationBillet lettreSommationBillet = lettreSommationBilletMapper.toEntity(request);
        lettreSommationBillet = lettreSommationBilletRepository.save(lettreSommationBillet);

        // Process file uploads and other operations...

        // Notify supervisors about the new document
        if (lettreSommationBillet.getAct() != null &&
                lettreSommationBillet.getAct().getAntenne() != null &&
                lettreSommationBillet.getAct().getAntenne().getSection() != null) {

            List<UtilisateurSysteme> supervisors = utilisateurSystemeRepository
                    .findByRoleAndAntenneSection(RoleUtilisateur.SUPERVISEUR,
                            lettreSommationBillet.getAct().getAntenne().getSection());

            for (UtilisateurSysteme supervisor : supervisors) {
                notificationService.createNotification(
                        supervisor,
                        TypeNotificationEnum.INFO,
                        "Nouvelle lettre de sommation (billet) créée pour l'agent " +
                                lettreSommationBillet.getAct().getNomPrenom() + " (Matricule: " +
                                lettreSommationBillet.getAct().getMatricule() + ")",
                        "/documents/lettre-billet/" + lettreSommationBillet.getId()
                );
            }
        }

        return lettreSommationBilletMapper.toDto(lettreSommationBillet);
    }




    @Transactional
    @Override
    public LettreSommationBilletResponse updateLettreSommationBillet(
            Long id, LettreSommationBilletRequest request, List<MultipartFile> fichiers) {

        // Get current user
        UtilisateurSysteme currentUser = (UtilisateurSysteme) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        LettreSommationBillet lettreSommationBillet = lettreSommationBilletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lettre de sommation billet non trouvée avec l'id: " + id));

        // Check if another lettre with the same numeroBillet already exists
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
                // Delete existing files from storage
                for (PieceJointe piece : lettreSommationBillet.getPiecesJointes()) {
                    fileStorageService.deleteFile(piece.getCheminFichier());
                }

                // Clear the existing pieces jointes
                lettreSommationBillet.getPiecesJointes().clear();

                // Add new pieces jointes
                for (MultipartFile fichier : fichiers) {
                    if (!fichier.isEmpty()) {
                        String fileName = fileStorageService.storeFile(fichier, "lettre-billet-" + id);

                        PieceJointe pieceJointe = PieceJointe.builder()
                                .typeDocument(TypeDocumentEnum.LETTRE_BILLET)
                                .documentId(id)
                                .nomFichier(fichier.getOriginalFilename())
                                .cheminFichier(fileName)
                                .typeMime(fichier.getContentType())
                                .taille(fichier.getSize())
                                .build();

                        lettreSommationBillet.addPieceJointe(pieceJointe);
                    }
                }

                // Track file uploads
                historiqueService.trackDocumentAction(
                        TypeDocumentEnum.LETTRE_BILLET,
                        id,
                        currentUser,
                        "Mise à jour des fichiers",
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

        // Map to response
        LettreSommationBilletResponse response = lettreSommationBilletMapper.toDto(lettreSommationBillet);

        return response;
    }

    @Transactional
    @Override
    public void deleteLettreSommationBillet(Long id) {
        LettreSommationBillet lettreSommationBillet = lettreSommationBilletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lettre de sommation billet non trouvée avec l'id: " + id));

        // Get current user
        UtilisateurSysteme currentUser = (UtilisateurSysteme) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        // Process file deletion and other operations...

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
                        "Une lettre de sommation (billet) a été supprimée pour l'agent " +
                                lettreSommationBillet.getAct().getNomPrenom() + " (Matricule: " +
                                lettreSommationBillet.getAct().getMatricule() + ")",
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
        List<LettreSommationBillet> lettres = lettreSommationBilletRepository.findAllById(request.getIds());

        if (lettres.size() != request.getIds().size()) {
            throw new ResourceNotFoundException("Une ou plusieurs lettres de sommation billet n'ont pas été trouvées");
        }

        // Get current user
        UtilisateurSysteme currentUser = (UtilisateurSysteme) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        for (LettreSommationBillet lettre : lettres) {
            // Store old status
            String oldStatus = lettre.getStatut() != null ? lettre.getStatut().toString() : null;

            // Update status
            lettre.setStatut(request.getNewStatus());

            // Add new comment if provided
            if (request.getCommentaire() != null && !request.getCommentaire().isEmpty()) {
                String existingComments = lettre.getCommentaires() != null ? lettre.getCommentaires() : "";
                String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String newComment = dateStr + " - Changement de statut à " + request.getNewStatus() + ": "
                        + request.getCommentaire();

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

        // Also notify the document creator (encadrant)
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