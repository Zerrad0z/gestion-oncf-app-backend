package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.dto.BulkUpdateStatusRequest;
import com.oncf.gare_app.dto.RapportMRequest;
import com.oncf.gare_app.dto.RapportMResponse;
import com.oncf.gare_app.dto.PieceJointeResponse;
import com.oncf.gare_app.entity.RapportM;
import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.CategorieRapportEnum;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.enums.TypeNotificationEnum;
import com.oncf.gare_app.enums.RoleUtilisateur;
import com.oncf.gare_app.exception.ResourceNotFoundException;
import com.oncf.gare_app.mapper.RapportMMapper;
import com.oncf.gare_app.mapper.PieceJointeMapper;
import com.oncf.gare_app.repository.RapportMRepository;
import com.oncf.gare_app.repository.PieceJointeRepository;
import com.oncf.gare_app.repository.UtilisateurSystemeRepository;
import com.oncf.gare_app.service.*;
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
public class RapportMServiceImpl implements RapportMService {

    @PersistenceContext
    private EntityManager entityManager;

    private final RapportMRepository rapportMRepository;
    private final PieceJointeRepository pieceJointeRepository;
    private final UtilisateurSystemeRepository utilisateurSystemeRepository;
    private final RapportMMapper rapportMMapper;
    private final PieceJointeMapper pieceJointeMapper;
    private final FileStorageService fileStorageService;
    private final HistoriqueService historiqueService;
    private final NotificationService notificationService;
    private final UtilisateurService utilisateurService;

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

        // Validate permissions and get current user
        utilisateurService.validateCanCreate();
        UtilisateurSysteme currentUser = utilisateurService.getCurrentUser();

        // Create rapport with current user
        RapportM rapportM = rapportMMapper.toEntity(request);
        rapportM.setUtilisateur(currentUser);

        // Save the entity FIRST to get the ID
        rapportM = rapportMRepository.save(rapportM);

        // Force flush to ensure the entity is saved and has an ID
        entityManager.flush();

        // Log the saved entity for debugging
        System.out.println("Saved RapportM with ID: " + rapportM.getId() +
                " by user: " + currentUser.getNomUtilisateur());

        // Process file uploads if provided
        if (fichiers != null && !fichiers.isEmpty()) {
            try {
                for (MultipartFile fichier : fichiers) {
                    if (!fichier.isEmpty()) {
                        // Store file and get path
                        String fileName = fileStorageService.storeFile(fichier, "rapport-m-" + rapportM.getId());

                        // Create PieceJointe entity
                        PieceJointe pieceJointe = PieceJointe.builder()
                                .typeDocument(TypeDocumentEnum.RAPPORT_M)
                                .documentId(rapportM.getId())
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
                TypeDocumentEnum.RAPPORT_M,
                rapportM.getId(),
                currentUser
        );

        // Notify supervisors about new document creation
        notifyDocumentCreation(rapportM);

        // Return the DTO
        return rapportMMapper.toDto(rapportM);
    }

    @Transactional
    @Override
    public RapportMResponse updateRapportM(Long id, RapportMRequest request, List<MultipartFile> fichiers) {

        // Validate permissions and get current user
        utilisateurService.validateCanUpdate();
        UtilisateurSysteme currentUser = utilisateurService.getCurrentUser();

        RapportM rapportM = rapportMRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rapport M non trouvé avec l'id: " + id));

        utilisateurService.validateCanEditDocument(rapportM.getUtilisateur());

        // Update entity fields
        rapportMMapper.updateEntityFromDto(request, rapportM);

        // Process file uploads if new files were provided
        if (fichiers != null && !fichiers.isEmpty()) {
            try {
                // Delete existing files from storage
                List<PieceJointe> existingPieces = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                        TypeDocumentEnum.RAPPORT_M, id);

                for (PieceJointe piece : existingPieces) {
                    fileStorageService.deleteFile(piece.getCheminFichier());
                    pieceJointeRepository.delete(piece);
                }

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
                                .dateUpload(LocalDateTime.now())
                                .build();

                        pieceJointeRepository.save(pieceJointe);
                    }
                }

                // Track file uploads
                historiqueService.trackDocumentAction(
                        TypeDocumentEnum.RAPPORT_M,
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
        rapportM = rapportMRepository.save(rapportM);

        // Track document update
        historiqueService.trackDocumentUpdate(
                TypeDocumentEnum.RAPPORT_M,
                id,
                currentUser,
                "Mise à jour générale"
        );

        // Map to response
        return rapportMMapper.toDto(rapportM);
    }

    @Transactional
    @Override
    public void deleteRapportM(Long id) {
        // Validate permissions and get current user
        utilisateurService.validateCanDelete();
        UtilisateurSysteme currentUser = utilisateurService.getCurrentUser();

        RapportM rapportM = rapportMRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rapport M non trouvé avec l'id: " + id));

        // Delete file attachments
        try {
            List<PieceJointe> piecesJointes = pieceJointeRepository.findByTypeDocumentAndDocumentId(
                    TypeDocumentEnum.RAPPORT_M, id);

            for (PieceJointe piece : piecesJointes) {
                fileStorageService.deleteFile(piece.getCheminFichier());
                pieceJointeRepository.delete(piece);
            }
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
                        "Un rapport " + rapportM.getCategorie() + " a été supprimé par " + currentUser.getNomPrenom() +
                                " pour l'agent " + rapportM.getAct().getNomPrenom() +
                                " (Matricule: " + rapportM.getAct().getMatricule() + ")",
                        "/documents/rapport-m"
                );
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<RapportMResponse> searchRapportsM(
            Long actId, CategorieRapportEnum categorie,
            String references, String objet, String detail,
            LocalDate dateDebut, LocalDate dateFin) {

        List<RapportMResponse> responses = rapportMRepository.search(
                        actId, categorie, references, objet, detail, dateDebut, dateFin)
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
    public List<RapportMResponse> getRapportMByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        return rapportMRepository.findByDateEnvoiBetween(dateDebut, dateFin).stream()
                .map(rapportMMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<RapportMResponse> updateBulk(BulkUpdateStatusRequest request) {
        // Validate permissions and get current user
        utilisateurService.validateCanUpdateBulkStatus();
        UtilisateurSysteme currentUser = utilisateurService.getCurrentUser();

        List<RapportM> rapports = rapportMRepository.findAllById(request.getIds());

        if (rapports.size() != request.getIds().size()) {
            throw new ResourceNotFoundException("Un ou plusieurs rapports M n'ont pas été trouvés");
        }

        for (RapportM rapport : rapports) {
            // Add new comment if provided
            if (request.getCommentaire() != null && !request.getCommentaire().isEmpty()) {
                String existingDetail = rapport.getDetail() != null ? rapport.getDetail() : "";
                String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String newComment = dateStr + " - Mise à jour par " + currentUser.getNomPrenom() + ": " +
                        request.getCommentaire();

                if (!existingDetail.isEmpty()) {
                    existingDetail += "\n\n";
                }

                rapport.setDetail(existingDetail + newComment);
            }

            // Track bulk update
            historiqueService.trackDocumentAction(
                    TypeDocumentEnum.RAPPORT_M,
                    rapport.getId(),
                    currentUser,
                    "MISE_A_JOUR_LOT",
                    "Mise à jour par lot: " + request.getCommentaire(),
                    null,
                    null
            );
        }

        rapports = rapportMRepository.saveAll(rapports);

        // Map to responses
        return rapports.stream()
                .map(rapportMMapper::toDto)
                .collect(Collectors.toList());
    }

    private void notifyDocumentCreation(RapportM rapport) {
        if (rapport.getAct() != null &&
                rapport.getAct().getAntenne() != null &&
                rapport.getAct().getAntenne().getSection() != null) {

            List<UtilisateurSysteme> supervisors = utilisateurSystemeRepository
                    .findByRoleAndAntenneSection(RoleUtilisateur.SUPERVISEUR,
                            rapport.getAct().getAntenne().getSection());

            for (UtilisateurSysteme supervisor : supervisors) {
                notificationService.createNotification(
                        supervisor,
                        TypeNotificationEnum.INFO,
                        "Un nouveau rapport " + rapport.getCategorie() + " a été créé par " +
                                rapport.getUtilisateur().getNomPrenom() + " pour l'agent " +
                                rapport.getAct().getNomPrenom() + " (Matricule: " +
                                rapport.getAct().getMatricule() + ")",
                        "/documents/rapport-m/" + rapport.getId()
                );
            }
        }
    }
}