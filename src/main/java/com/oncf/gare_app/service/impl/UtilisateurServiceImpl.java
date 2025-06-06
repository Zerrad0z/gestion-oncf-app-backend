package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.dto.UtilisateurRequest;
import com.oncf.gare_app.dto.UtilisateurResponse;
import com.oncf.gare_app.dto.UtilisateurUpdateRequest;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.RoleUtilisateur;
import com.oncf.gare_app.mapper.UtilisateurMapper;
import com.oncf.gare_app.repository.UtilisateurSystemeRepository;
import com.oncf.gare_app.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurSystemeRepository utilisateurSystemeRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UtilisateurSysteme getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return utilisateurSystemeRepository.findByNomUtilisateur(username)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + username));
        }

        throw new RuntimeException("Utilisateur non authentifié");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> getAllUtilisateurs() {
        return utilisateurSystemeRepository.findAll().stream()
                .map(utilisateurMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponse getUtilisateurById(Long id) {
        UtilisateurSysteme utilisateur = utilisateurSystemeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));
        return utilisateurMapper.toDto(utilisateur);
    }

    @Override
    @Transactional
    public UtilisateurResponse createUtilisateur(UtilisateurRequest request) {
        // Check if username already exists
        if (utilisateurSystemeRepository.existsByNomUtilisateur(request.getNomUtilisateur())) {
            throw new RuntimeException("Le nom d'utilisateur existe déjà: " + request.getNomUtilisateur());
        }

        // Check if email already exists
        if (utilisateurSystemeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("L'email existe déjà: " + request.getEmail());
        }

        // Create new user
        UtilisateurSysteme utilisateur = utilisateurMapper.toEntity(request);
        utilisateur.setMotDePasseHash(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setActif(true);

        utilisateur = utilisateurSystemeRepository.save(utilisateur);
        return utilisateurMapper.toDto(utilisateur);
    }

    @Override
    @Transactional
    public UtilisateurResponse updateUtilisateur(Long id, UtilisateurUpdateRequest request) {
        UtilisateurSysteme utilisateur = utilisateurSystemeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));

        // Check if new username already exists (if changed)
        if (request.getNomUtilisateur() != null &&
                !utilisateur.getNomUtilisateur().equals(request.getNomUtilisateur()) &&
                utilisateurSystemeRepository.existsByNomUtilisateur(request.getNomUtilisateur())) {
            throw new RuntimeException("Le nom d'utilisateur existe déjà: " + request.getNomUtilisateur());
        }

        // Check if new email already exists (if changed)
        if (request.getEmail() != null &&
                !utilisateur.getEmail().equals(request.getEmail()) &&
                utilisateurSystemeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("L'email existe déjà: " + request.getEmail());
        }

        // Update fields
        utilisateurMapper.updateEntityFromDto(request, utilisateur);

        // Update password if provided
        if (request.getMotDePasse() != null && !request.getMotDePasse().isEmpty()) {
            utilisateur.setMotDePasseHash(passwordEncoder.encode(request.getMotDePasse()));
        }

        utilisateur = utilisateurSystemeRepository.save(utilisateur);
        return utilisateurMapper.toDto(utilisateur);
    }

    @Override
    @Transactional
    public void deleteUtilisateur(Long id) {
        if (!utilisateurSystemeRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé avec l'id: " + id);
        }
        utilisateurSystemeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UtilisateurResponse updateDerniereConnexion(Long id) {
        UtilisateurSysteme utilisateur = utilisateurSystemeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));

        utilisateur.setDerniereConnexion(LocalDate.now());
        utilisateur = utilisateurSystemeRepository.save(utilisateur);
        return utilisateurMapper.toDto(utilisateur);
    }

    @Override
    @Transactional
    public UtilisateurResponse toggleStatus(Long id) {
        UtilisateurSysteme utilisateur = utilisateurSystemeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));

        utilisateur.setActif(!utilisateur.isActif());
        utilisateur = utilisateurSystemeRepository.save(utilisateur);
        return utilisateurMapper.toDto(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    @Override
    @Transactional(readOnly = true)
    public String getCurrentUsername() {
        return getCurrentUser().getNomUtilisateur();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(RoleUtilisateur role) {
        return getCurrentUser().getRole() == role;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEncadrant() {
        return hasRole(RoleUtilisateur.ENCADRANT);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSuperviseur() {
        return hasRole(RoleUtilisateur.SUPERVISEUR);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAdmin() {
        return hasRole(RoleUtilisateur.ADMIN);
    }

    @Override
    public void validateCanCreate() {
        if (!isEncadrant()) {
            throw new RuntimeException("Seuls les encadrants peuvent créer des documents");
        }
    }

    @Override
    public void validateCanUpdate() {
        if (!isEncadrant() && !isAdmin()) {
            throw new RuntimeException("Seuls les encadrants et administrateurs peuvent modifier des documents");
        }
    }

    @Override
    public void validateCanDelete() {
        if (!isAdmin()) {
            throw new RuntimeException("Seuls les administrateurs peuvent supprimer des documents");
        }
    }

    @Override
    public void validateCanUpdateBulkStatus() {
        if (!isSuperviseur() && !isAdmin()) {
            throw new RuntimeException("Seuls les superviseurs et administrateurs peuvent modifier le statut en lot");
        }
    }

    @Override
    public void validateCanEditDocument(UtilisateurSysteme documentOwner) {
        UtilisateurSysteme currentUser = getCurrentUser();

        // Admin can edit any document
        if (isAdmin()) {
            return;
        }

        // Encadrant can only edit their own documents
        if (isEncadrant()) {
            if (!documentOwner.getId().equals(currentUser.getId())) {
                throw new RuntimeException("Vous ne pouvez modifier que vos propres documents");
            }
            return;
        }

        // Others cannot edit
        throw new RuntimeException("Vous n'avez pas les droits pour modifier ce document");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canEditDocument(Long documentOwnerId) {
        try {
            UtilisateurSysteme documentOwner = utilisateurSystemeRepository.findById(documentOwnerId)
                    .orElseThrow(() -> new RuntimeException("Propriétaire du document non trouvé"));
            validateCanEditDocument(documentOwner);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}