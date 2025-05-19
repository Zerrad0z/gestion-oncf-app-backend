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
public class LettreSommationCarteServiceImpl implements LettreSommationCarteService {

    private final LettreSommationCarteRepository lettreSommationCarteRepository;
    private final PieceJointeRepository pieceJointeRepository;
    private final UtilisateurSystemeRepository utilisateurSystemeRepository;
    private final LettreSommationCarteMapper lettreSommationCarteMapper;
    private final PieceJointeMapper pieceJointeMapper;
    private final FileStorageService fileStorageService;
    private final HistoriqueService historiqueService;
    private final NotificationService notificationService;

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

        // Get current user (encadrant)
        UtilisateurSysteme currentUser = (UtilisateurSysteme) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        // Check if a lettre with the same numeroCarte already exists
        if (lettreSommationCarteRepository.existsByNumeroCarte(request.getNumeroCarte())) {
            throw new RuntimeException("Une lettre de sommation carte avec le numéro " + request.getNumeroCarte() + " existe déjà");
        }

        // Create lettre sommation
        LettreSommationCarte lettreSommationCarte = lettreSommationCarteMapper.toEntity(request);
        lettreSommationCarte = lettreSommationCarteRepository.save(lettreSommationCarte);

        // Track document creation
        historiqueService.trackDocumentCreation(
                TypeDocumentEnum.LETTRE_CARTE,
                lettreSommationCarte.getId(),
                currentUser
        );

        // Process file uploads
        if (fichiers != null && !fichiers.isEmpty()) {
            try {
                for (MultipartFile fichier : fichiers) {
                    if (!fichier.isEmpty()) {
                        String fileName = fileStorageService.storeFile(fichier, "lettre-carte-" + lettreSommationCarte.getId());

                        PieceJointe pieceJointe = PieceJointe.builder()
                                .typeDocument(TypeDocumentEnum.LETTRE_CARTE)
                                .documentId(lettreSommationCarte.getId())
                                .nomFichier(fichier.getOriginalFilename())
                                .cheminFichier(fileName)
                                .typeMime(fichier.getContentType())
                                .taille(fichier.getSize())
                                .build();

                        // Add piece jointe to lettre
                        lettreSommationCarte.addPieceJointe(pieceJointe);
                    }
                }

                // Save the updated entity
                lettreSommationCarte = lettreSommationCarteRepository.save(lettreSommationCarte);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'enregistrement des fichiers", e);
            }
        }

        // Notify supervisors about the new document
        if (lettreSommationCarte.getAct() != null &&
                lettreSommationCarte.getAct().getAntenne() != null &&
                lettreSommationCarte.getAct().getAntenne().getSection() != null) {

            List<UtilisateurSysteme> supervisors = utilisateurSystemeRepository
                    .findByRoleAndAntenneSection(RoleUtilisateur.SUPERVISEUR,
                            lettreSommationCarte.getAct().getAntenne().getSection());

            for (UtilisateurSysteme supervisor : supervisors) {
                notificationService.createNotification(
                        supervisor,
                        TypeNotificationEnum.INFO,
                        "Nouvelle lettre de sommation (carte) créée pour l'agent " +
                                lettreSommationCarte.getAct().getNomPrenom() + " (Matricule: " +
                                lettreSommationCarte.getAct().getMatricule() + ")",
                        "/documents/lettre-carte/" + lettreSommationCarte.getId()
                );
            }
        }

        LettreSommationCarteResponse response = lettreSommationCarteMapper.toDto(lettreSommationCarte);

        return response;
    }

    @Transactional
    @Override
    public LettreSommationCarteResponse updateLettreSommationCarte(
            Long id, LettreSommationCarteRequest request, List<MultipartFile> fichiers) {

        // Get current user (encadrant)
        UtilisateurSysteme currentUser = (UtilisateurSysteme) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        LettreSommationCarte lettreSommationCarte = lettreSommationCarteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lettre de sommation carte non trouvée avec l'id: " + id));

        // Check if another lettre with the same numeroCarte already exists
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
                for (PieceJointe piece : lettreSommationCarte.getPiecesJointes()) {
                    fileStorageService.deleteFile(piece.getCheminFichier());
                }

                // Clear the existing pieces jointes
                lettreSommationCarte.getPiecesJointes().clear();

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
                                .build();

                        lettreSommationCarte.addPieceJointe(pieceJointe);
                    }
                }

                // Track file uploads
                historiqueService.trackDocumentAction(
                        TypeDocumentEnum.LETTRE_CARTE,
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
        LettreSommationCarteResponse response = lettreSommationCarteMapper.toDto(lettreSommationCarte);

        return response;
    }

    @Transactional
    @Override
    public void deleteLettreSommationCarte(Long id) {
        LettreSommationCarte lettreSommationCarte = lettreSommationCarteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lettre de sommation carte non trouvée avec l'id: " + id));

        // Get current user (encadrant)
        UtilisateurSysteme currentUser = (UtilisateurSysteme) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        // Delete file attachments
        try {
            for (PieceJointe piece : lettreSommationCarte.getPiecesJointes()) {
                fileStorageService.deleteFile(piece.getCheminFichier());
            }

            // The pieces jointes will be deleted automatically due to CascadeType.ALL and orphanRemoval=true
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
                        "Une lettre de sommation (carte) a été supprimée pour l'agent " +
                                lettreSommationCarte.getAct().getNomPrenom() + " (Matricule: " +
                                lettreSommationCarte.getAct().getMatricule() + ")",
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
        List<LettreSommationCarte> lettres = lettreSommationCarteRepository.findAllById(request.getIds());

        if (lettres.size() != request.getIds().size()) {
            throw new ResourceNotFoundException("Une ou plusieurs lettres de sommation carte n'ont pas été trouvées");
        }

        // Get current user (encadrant)
        UtilisateurSysteme currentUser = (UtilisateurSysteme) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        for (LettreSommationCarte lettre : lettres) {
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
        List<LettreSommationCarteResponse> responses = lettres.stream()
                .map(lettreSommationCarteMapper::toDto)
                .collect(Collectors.toList());

        return responses;
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