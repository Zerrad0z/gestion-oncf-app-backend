package com.oncf.gare_app.service;

import com.oncf.gare_app.dto.UtilisateurRequest;
import com.oncf.gare_app.dto.UtilisateurResponse;
import com.oncf.gare_app.dto.UtilisateurUpdateRequest;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.RoleUtilisateur;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UtilisateurService {

    @Transactional(readOnly = true)
    List<UtilisateurResponse> getAllUtilisateurs();

    @Transactional(readOnly = true)
    UtilisateurResponse getUtilisateurById(Long id);

    @Transactional
    UtilisateurResponse createUtilisateur(UtilisateurRequest request);

    @Transactional
    UtilisateurResponse updateUtilisateur(Long id, UtilisateurUpdateRequest request);

    @Transactional
    void deleteUtilisateur(Long id);

    @Transactional
    UtilisateurResponse updateDerniereConnexion(Long id);

    @Transactional
    UtilisateurResponse toggleStatus(Long id);

    // Authentication helper methods
    @Transactional(readOnly = true)
    UtilisateurSysteme getCurrentUser();

    @Transactional(readOnly = true)
    Long getCurrentUserId();

    @Transactional(readOnly = true)
    String getCurrentUsername();

    @Transactional(readOnly = true)
    boolean hasRole(RoleUtilisateur role);

    @Transactional(readOnly = true)
    boolean isEncadrant();

    @Transactional(readOnly = true)
    boolean isSuperviseur();

    @Transactional(readOnly = true)
    boolean isAdmin();

    // Permission validation methods
    void validateCanCreate();
    void validateCanUpdate();
    void validateCanDelete();
    void validateCanUpdateBulkStatus();
    void validateCanEditDocument(UtilisateurSysteme documentOwner);

    @Transactional(readOnly = true)
    boolean canEditDocument(Long documentOwnerId);
}