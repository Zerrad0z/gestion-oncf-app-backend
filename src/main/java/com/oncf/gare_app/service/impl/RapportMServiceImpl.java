package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.dto.BulkUpdateStatusRequest;
import com.oncf.gare_app.dto.RapportMRequest;
import com.oncf.gare_app.dto.RapportMResponse;
import com.oncf.gare_app.dto.PieceJointeResponse;
import com.oncf.gare_app.entity.RapportM;
import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.CategorieRapportEnum;
import com.oncf.gare_app.enums.StatutEnum;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.enums.TypeNotificationEnum;
import com.oncf.gare_app.enums.RoleUtilisateur;
import com.oncf.gare_app.exception.ResourceNotFoundException;
import com.oncf.gare_app.mapper.RapportMMapper;
import com.oncf.gare_app.mapper.PieceJointeMapper;
import com.oncf.gare_app.repository.RapportMRepository;
import com.oncf.gare_app.repository.PieceJointeRepository;
import com.oncf.gare_app.repository.UtilisateurSystemeRepository;
import com.oncf.gare_app.service.FileStorageService;
import com.oncf.gare_app.service.HistoriqueService;
import com.oncf.gare_app.service.RapportMService;
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
public class RapportMServiceImpl implements RapportMService {

    private final RapportMRepository rapportMRepository;
    private final PieceJointeRepository pieceJointeRepository;
    private final UtilisateurSystemeRepository utilisateurSystemeRepository;
    private final RapportMMapper rapportMMapper;
    private final PieceJointeMapper pieceJointeMapper;
    private final FileStorageService fileStorageService;
    private final HistoriqueService historiqueService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    @Override
    public List<RapportMResponse> getAllRapportsM() {
        List<RapportMResponse> responses = rapportMRepository.findAll().stream()
                .map(rapportMMapper::toDto)
                .collect(Collectors.toList());

        // Load piece jointes for each rapport
        for (RapportMResponse response : responses) {
            List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                    TypeDocumentEnum.RAPPORT_M, response.getId());

            List<PieceJointeResponse> pieceJointeResponses = piecesJointes.stream()
                    .map(pieceJointeMapper::toDto)
                    .collect(Collectors.toList());

            response.setPiecesJointes(pieceJointeResponses);
        }

        return responses;
    }

    @Transactional(readOnly = true)
    @Override
    public RapportMResponse getRapportMById(Long id) {
        RapportM rapportM = rapportMRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rapport M non trouvé avec l'id: " + id));

        RapportMResponse response = rapportMMapper.toDto(rapportM);

        // Load piece jointes
        List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                TypeDocumentEnum.RAPPORT_M, id);

        List<PieceJointeResponse> pieceJointeResponses = piecesJointes.stream()
                .map(pieceJointeMapper::toDto)
                .collect(Collectors.toList());

        response.setPiecesJointes(pieceJointeResponses);

        return response;
    }

    @Transactional
    @Override
    public RapportMResponse createRapportM(RapportMRequest request, List<MultipartFile> fichiers) {
        // Get current user (encadrant)
        UtilisateurSysteme currentUser = (UtilisateurSysteme) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        // Create rapport
        RapportM rapportM = rapportMMapper.toEntity(request);
        rapportM = rapportMRepository.save(rapportM);

        // Track document creation
        historiqueService.trackDocumentCreation(
                TypeDocumentEnum.RAPPORT_M,
                rapportM.getId(),
                currentUser
        );

        // Process file uploads
        if (fichiers != null && !fichiers.isEmpty()) {
            try {
                for (MultipartFile fichier : fichiers) {
                    if (!fichier.isEmpty()) {
                        String fileName = fileStorageService.storeFile(fichier, "rapport-m-" + rapportM.getId());

                        PieceJointe pieceJointe = PieceJointe.builder()
                                .typeDocument(TypeDocumentEnum.RAPPORT_M)
                                .documentId(rapportM.getId())
                                .nomFichier(fichier.getOriginalFilename())
                                .cheminFichier(fileName)
                                .typeMime(fichier.getContentType())
                                .taille(fichier.getSize())
                                .build();

                        // Add piece jointe to rapport
                        rapportM.addPieceJointe(pieceJointe);
                    }
                }

                // Save the updated entity
                rapportM = rapportMRepository.save(rapportM);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'enregistrement des fichiers", e);
            }
        }

        // Notify supervisors about the new document
        if (rapportM.getAct() != null &&
                rapportM.getAct().getAntenne() != null &&
                rapportM.getAct().getAntenne().getSection() != null) {

            List<UtilisateurSysteme> supervisors = utilisateurSystemeRepository
                    .findByRoleAndAntenneSection(RoleUtilisateur.SUPERVISEUR,
                            rapportM.getAct().getAntenne().getSection());

            // Determine notification type based on rapport category and priority
            TypeNotificationEnum notificationType = TypeNotificationEnum.INFO;
            if (rapportM.getPriorite() != null && rapportM.getPriorite() > 3) {
                notificationType = TypeNotificationEnum.ALERTE;
            }
            if (rapportM.getCategorie() == CategorieRapportEnum.COMPTABILITE) {
                notificationType = TypeNotificationEnum.ALERTE;
            }

            for (UtilisateurSysteme supervisor : supervisors) {
                notificationService.createNotification(
                        supervisor,
                        notificationType,
                        "Nouveau rapport " + rapportM.getCategorie() + " créé par l'agent " +
                                rapportM.getAct().getNomPrenom() + " (Matricule: " +
                                rapportM.getAct().getMatricule() + ") : " + rapportM.getTitre(),
                        "/documents/rapport-m/" + rapportM.getId()
                );
            }
        }

        RapportMResponse response = rapportMMapper.toDto(rapportM);

        return response;
    }

    @Transactional
    @Override
    public RapportMResponse updateRapportM(Long id, RapportMRequest request, List<MultipartFile> fichiers) {
        // Get current user (encadrant)
        UtilisateurSysteme currentUser = (UtilisateurSysteme) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        RapportM rapportM = rapportMRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rapport M non trouvé avec l'id: " + id));

        // Store old status for history tracking
        String oldStatus = rapportM.getStatut() != null ?
                rapportM.getStatut().toString() : null;

        // Store old priority for notification
        Integer oldPriority = rapportM.getPriorite();

        // Update entity fields
        rapportMMapper.updateEntityFromDto(request, rapportM);

        // Process file uploads if new files were provided
        if (fichiers != null && !fichiers.isEmpty()) {
            try {
                // Delete existing files from storage
                for (PieceJointe piece : rapportM.getPiecesJointes()) {
                    fileStorageService.deleteFile(piece.getCheminFichier());
                }

                // Clear the existing pieces jointes
                rapportM.getPiecesJointes().clear();

                // Add new pieces jointes
                for (MultipartFile fichier : fichiers) {
                    if (!fichier.isEmpty()) {
                        String fileName = fileStorageService.storeFile(fichier, "rapport-m-" + id);

                        PieceJointe pieceJointe = PieceJointe.builder()
                                .typeDocument(TypeDocumentEnum.RAPPORT_M)
                                .documentId(id)
                                .nomFichier(fichier.getOriginalFilename())
                                .cheminFichier(fileName)
                                .typeMime(fichier.getContentType())
                                .taille(fichier.getSize())
                                .build();

                        rapportM.addPieceJointe(pieceJointe);
                    }
                }

                // Track file uploads
                historiqueService.trackDocumentAction(
                        TypeDocumentEnum.RAPPORT_M,
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
        rapportM = rapportMRepository.save(rapportM);

        // Track document update
        historiqueService.trackDocumentUpdate(
                TypeDocumentEnum.RAPPORT_M,
                id,
                currentUser,
                "Mise à jour générale"
        );

        // Track status change if applicable
        String newStatus = rapportM.getStatut() != null ?
                rapportM.getStatut().toString() : null;

        if (oldStatus != null && newStatus != null && !oldStatus.equals(newStatus)) {
            historiqueService.trackDocumentStatusChange(
                    TypeDocumentEnum.RAPPORT_M,
                    rapportM.getId(),
                    currentUser,
                    oldStatus,
                    newStatus,
                    "Changement de statut"
            );

            // Notify supervisors about status change
            notifyStatusChange(rapportM, oldStatus, newStatus);
        }

        // Notify supervisors if priority has increased
        if (oldPriority != null && rapportM.getPriorite() != null &&
                rapportM.getPriorite() > oldPriority && rapportM.getPriorite() >= 3) {

            if (rapportM.getAct() != null &&
                    rapportM.getAct().getAntenne() != null &&
                    rapportM.getAct().getAntenne().getSection() != null) {

                List<UtilisateurSysteme> supervisors = utilisateurSystemeRepository
                        .findByRoleAndAntenneSection(RoleUtilisateur.SUPERVISEUR,
                                rapportM.getAct().getAntenne().getSection());

                for (UtilisateurSysteme supervisor : supervisors) {
                    notificationService.createNotification(
                            supervisor,
                            TypeNotificationEnum.ALERTE,
                            "La priorité du rapport " + rapportM.getTitre() + " a été augmentée de " +
                                    oldPriority + " à " + rapportM.getPriorite(),
                            "/documents/rapport-m/" + rapportM.getId()
                    );
                }
            }
        }

        // Map to response
        RapportMResponse response = rapportMMapper.toDto(rapportM);

        return response;
    }

    @Transactional
    @Override
    public void deleteRapportM(Long id) {
        RapportM rapportM = rapportMRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rapport M non trouvé avec l'id: " + id));

        // Get current user (encadrant)
        UtilisateurSysteme currentUser = (UtilisateurSysteme) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        // Store report info for notifications before deletion
        String reportTitle = rapportM.getTitre();
        CategorieRapportEnum reportCategory = rapportM.getCategorie();

        // Delete file attachments
        try {
            for (PieceJointe piece : rapportM.getPiecesJointes()) {
                fileStorageService.deleteFile(piece.getCheminFichier());
            }

            // The pieces jointes will be deleted automatically due to CascadeType.ALL and orphanRemoval=true
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la suppression des fichiers", e);
        }

        // Track document deletion
        historiqueService.trackDocumentDeletion(
                TypeDocumentEnum.RAPPORT_M,
                id,
                currentUser
        );

        // Delete rapport
        rapportMRepository.deleteById(id);

        // Notify supervisors about deletion
        if (rapportM.getAct() != null &&
                rapportM.getAct().getAntenne() != null &&
                rapportM.getAct().getAntenne().getSection() != null) {

            List<UtilisateurSysteme> supervisors = utilisateurSystemeRepository
                    .findByRoleAndAntenneSection(RoleUtilisateur.SUPERVISEUR,
                            rapportM.getAct().getAntenne().getSection());

            for (UtilisateurSysteme supervisor : supervisors) {
                notificationService.createNotification(
                        supervisor,
                        TypeNotificationEnum.ALERTE,
                        "Le rapport " + reportCategory + " : " + reportTitle + " a été supprimé pour l'agent " +
                                rapportM.getAct().getNomPrenom() + " (Matricule: " +
                                rapportM.getAct().getMatricule() + ")",
                        "/documents/rapport-m"
                );
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<RapportMResponse> searchRapportsM(
            Long actId, CategorieRapportEnum categorie, StatutEnum statut,
            String titre, String contenu, Integer priorite,
            LocalDate dateDebut, LocalDate dateFin) {

        List<RapportMResponse> responses = rapportMRepository.search(
                        actId, categorie, statut, titre, contenu, priorite, dateDebut, dateFin)
                .stream()
                .map(rapportMMapper::toDto)
                .collect(Collectors.toList());

        // Load piece jointes for each rapport
        for (RapportMResponse response : responses) {
            List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                    TypeDocumentEnum.RAPPORT_M, response.getId());

            List<PieceJointeResponse> pieceJointeResponses = piecesJointes.stream()
                    .map(pieceJointeMapper::toDto)
                    .collect(Collectors.toList());

            response.setPiecesJointes(pieceJointeResponses);
        }

        return responses;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RapportMResponse> getRapportMByActId(Long actId) {
        return rapportMRepository.findByActId(actId).stream()
                .map(rapportMMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<RapportMResponse> getRapportMByCategorie(CategorieRapportEnum categorie) {
        return rapportMRepository.findByCategorie(categorie).stream()
                .map(rapportMMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<RapportMResponse> getRapportMByStatut(StatutEnum statut) {
        return rapportMRepository.findByStatut(statut).stream()
                .map(rapportMMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<RapportMResponse> getRapportMByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        return rapportMRepository.findByDateCreationBetween(dateDebut, dateFin).stream()
                .map(rapportMMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<RapportMResponse> getRapportMByPriorite(Integer priorite) {
        return rapportMRepository.findByPriorite(priorite).stream()
                .map(rapportMMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<RapportMResponse> updateBulkStatus(BulkUpdateStatusRequest request) {
        List<RapportM> rapports = rapportMRepository.findAllById(request.getIds());

        if (rapports.size() != request.getIds().size()) {
            throw new ResourceNotFoundException("Un ou plusieurs rapports M n'ont pas été trouvés");
        }

        // Get current user (encadrant)
        UtilisateurSysteme currentUser = (UtilisateurSysteme) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        for (RapportM rapport : rapports) {
            // Store old status
            String oldStatus = rapport.getStatut() != null ? rapport.getStatut().toString() : null;

            // Update status
            rapport.setStatut(request.getNewStatus());

            // Add new comment if provided
            if (request.getCommentaire() != null && !request.getCommentaire().isEmpty()) {
                String existingContent = rapport.getContenu() != null ? rapport.getContenu() : "";
                String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String newComment = "\n\n" + dateStr + " - Changement de statut à " + request.getNewStatus() + ": "
                        + request.getCommentaire();

                rapport.setContenu(existingContent + newComment);
            }

            // Set treatment date if status indicates treatment is completed
            if (request.getNewStatus() == StatutEnum.TRAITE ||
                    request.getNewStatus() == StatutEnum.REJETE) {
                rapport.setDateTraitement(LocalDate.now());
            }

            // Track status change
            historiqueService.trackDocumentStatusChange(
                    TypeDocumentEnum.RAPPORT_M,
                    rapport.getId(),
                    currentUser,
                    oldStatus,
                    request.getNewStatus().toString(),
                    "Changement de statut par lot: " + request.getCommentaire()
            );

            // Notify about status change
            notifyStatusChange(rapport, oldStatus, request.getNewStatus().toString());
        }

        rapports = rapportMRepository.saveAll(rapports);

        // Map to responses
        List<RapportMResponse> responses = rapports.stream()
                .map(rapportMMapper::toDto)
                .collect(Collectors.toList());

        return responses;
    }

    private void notifyStatusChange(RapportM rapport, String oldStatus, String newStatus) {
        // Determine notification type based on the new status and report category/priority
        TypeNotificationEnum notificationType = TypeNotificationEnum.INFO;
        if (newStatus.contains("REJETE")) {
            notificationType = TypeNotificationEnum.ALERTE;
        } else if (rapport.getPriorite() != null && rapport.getPriorite() >= 4) {
            notificationType = TypeNotificationEnum.URGENT;
        } else if (rapport.getCategorie() == CategorieRapportEnum.COMPTABILITE) {
            notificationType = TypeNotificationEnum.ALERTE;
        }

        // Notify the supervisors
        if (rapport.getAct() != null &&
                rapport.getAct().getAntenne() != null &&
                rapport.getAct().getAntenne().getSection() != null) {

            List<UtilisateurSysteme> supervisors = utilisateurSystemeRepository
                    .findByRoleAndAntenneSection(RoleUtilisateur.SUPERVISEUR,
                            rapport.getAct().getAntenne().getSection());

            for (UtilisateurSysteme supervisor : supervisors) {
                notificationService.createNotification(
                        supervisor,
                        notificationType,
                        "Le rapport " + rapport.getCategorie() + " : " + rapport.getTitre() +
                                " a changé de statut de \"" + oldStatus + "\" à \"" + newStatus +
                                "\" pour l'agent " + rapport.getAct().getNomPrenom() +
                                " (Matricule: " + rapport.getAct().getMatricule() + ")",
                        "/documents/rapport-m/" + rapport.getId()
                );
            }
        }

        // Also notify the document creator (encadrant)
        if (rapport.getUtilisateur() != null) {
            notificationService.createNotification(
                    rapport.getUtilisateur(),
                    notificationType,
                    "Le statut du rapport " + rapport.getCategorie() + " : " + rapport.getTitre() +
                            " a été modifié de \"" + oldStatus + "\" à \"" + newStatus + "\"",
                    "/documents/rapport-m/" + rapport.getId()
            );
        }
    }
}