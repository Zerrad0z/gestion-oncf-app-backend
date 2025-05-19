package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.dto.UtilisateurRequest;
import com.oncf.gare_app.dto.UtilisateurResponse;
import com.oncf.gare_app.dto.UtilisateurUpdateRequest;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.TypeAction;
import com.oncf.gare_app.enums.TypeEntite;
import com.oncf.gare_app.exception.ResourceNotFoundException;
import com.oncf.gare_app.exception.UniqueConstraintViolationException;
import com.oncf.gare_app.mapper.UtilisateurMapper;
import com.oncf.gare_app.repository.UtilisateurSystemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilisateurService implements com.oncf.gare_app.service.UtilisateurService {

    private final UtilisateurSystemeRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
   // private final AuditService auditService;

    // Get current user from security context
    @Override
    public UtilisateurSysteme getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return utilisateurRepository.findByNomUtilisateur(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + username));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UtilisateurResponse> getAllUtilisateurs() {
        return utilisateurRepository.findAll().stream()
                .map(utilisateurMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public UtilisateurResponse getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id)
                .map(utilisateurMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));
    }

    @Transactional
    @Override
    public UtilisateurResponse createUtilisateur(UtilisateurRequest request) {
        // Check unique constraints
        if (utilisateurRepository.existsByNomUtilisateur(request.getNomUtilisateur())) {
            throw new UniqueConstraintViolationException("Ce nom d'utilisateur est déjà utilisé");
        }
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new UniqueConstraintViolationException("Cet email est déjà utilisé");
        }
        if (utilisateurRepository.existsByMatricule(request.getMatricule())) {
            throw new UniqueConstraintViolationException("Ce matricule est déjà utilisé");
        }

        UtilisateurSysteme utilisateur = utilisateurMapper.toEntity(request);
        utilisateur = utilisateurRepository.save(utilisateur);

        // Log the creation
//        UtilisateurSysteme currentUser = getCurrentUser();
//        auditService.logActivity(
//                TypeEntite.UTILISATEUR_SYSTEME,
//                utilisateur.getId(),
//                TypeAction.CREATION,
//                currentUser,
//                "Création de l'utilisateur " + utilisateur.getNomUtilisateur()
//        );

        return utilisateurMapper.toDto(utilisateur);
    }

    @Transactional
    @Override
    public UtilisateurResponse updateUtilisateur(Long id, UtilisateurUpdateRequest request) {
        UtilisateurSysteme utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));

        // Store original state for audit
        UtilisateurSysteme originalUser = utilisateurMapper.clone(utilisateur);

        // Check unique constraints if values changed
        if (request.getNomUtilisateur() != null &&
                !request.getNomUtilisateur().equals(utilisateur.getNomUtilisateur()) &&
                utilisateurRepository.existsByNomUtilisateur(request.getNomUtilisateur())) {
            throw new UniqueConstraintViolationException("Ce nom d'utilisateur est déjà utilisé");
        }
        if (request.getEmail() != null &&
                !request.getEmail().equals(utilisateur.getEmail()) &&
                utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new UniqueConstraintViolationException("Cet email est déjà utilisé");
        }
        if (request.getMatricule() != null &&
                !request.getMatricule().equals(utilisateur.getMatricule()) &&
                utilisateurRepository.existsByMatricule(request.getMatricule())) {
            throw new UniqueConstraintViolationException("Ce matricule est déjà utilisé");
        }

        utilisateurMapper.updateEntityFromDto(request, utilisateur);
        utilisateur = utilisateurRepository.save(utilisateur);

        // Log the update
        UtilisateurSysteme currentUser = getCurrentUser();
//        auditService.logObjectChange(
//                TypeEntite.UTILISATEUR_SYSTEME,
//                utilisateur.getId(),
//                TypeAction.MODIFICATION,
//                currentUser,
//                originalUser,
//                utilisateur,
//                "Modification de l'utilisateur " + utilisateur.getNomUtilisateur()
//        );

        return utilisateurMapper.toDto(utilisateur);
    }

    @Transactional
    @Override
    public void deleteUtilisateur(Long id) {
        UtilisateurSysteme utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));

        String username = utilisateur.getNomUtilisateur();
        utilisateurRepository.deleteById(id);

        // Log the deletion
        UtilisateurSysteme currentUser = getCurrentUser();
//        auditService.logActivity(
//                TypeEntite.UTILISATEUR_SYSTEME,
//                id,
//                TypeAction.SUPPRESSION,
//                currentUser,
//                "Suppression de l'utilisateur " + username
//        );
    }

    @Transactional
    @Override
    public UtilisateurResponse updateDerniereConnexion(Long id) {
        UtilisateurSysteme utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));

        utilisateur.setDerniereConnexion(LocalDate.now());
        utilisateur = utilisateurRepository.save(utilisateur);

        // Log the connection
//        auditService.logActivity(
//                TypeEntite.UTILISATEUR_SYSTEME,
//                utilisateur.getId(),
//                TypeAction.CONNEXION,
//                utilisateur,
//                "Connexion de l'utilisateur " + utilisateur.getNomUtilisateur()
//        );

        return utilisateurMapper.toDto(utilisateur);
    }

    @Transactional
    @Override
    public UtilisateurResponse toggleStatus(Long id) {
        UtilisateurSysteme utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));

        boolean oldStatus = utilisateur.isActif();
        utilisateur.setActif(!oldStatus);
        utilisateur = utilisateurRepository.save(utilisateur);

        // Log the status change
        UtilisateurSysteme currentUser = getCurrentUser();
//        auditService.logStatusChange(
//                TypeEntite.UTILISATEUR_SYSTEME,
//                utilisateur.getId(),
//                TypeAction.CHANGEMENT_STATUT,
//                currentUser,
//                oldStatus ? "ACTIF" : "INACTIF",
//                utilisateur.isActif() ? "ACTIF" : "INACTIF",
//                "Changement de statut de l'utilisateur " + utilisateur.getNomUtilisateur()
//        );

        return utilisateurMapper.toDto(utilisateur);
    }
}