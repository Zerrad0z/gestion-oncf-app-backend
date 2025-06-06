package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.dto.ChangePasswordRequest;
import com.oncf.gare_app.dto.LoginRequest;
import com.oncf.gare_app.dto.LoginResponse;
import com.oncf.gare_app.dto.UtilisateurResponse;
import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.exception.BadRequestException;
import com.oncf.gare_app.exception.ResourceNotFoundException;
import com.oncf.gare_app.mapper.UtilisateurMapper;
import com.oncf.gare_app.repository.UtilisateurSystemeRepository;
import com.oncf.gare_app.security.JwtUtil;
import com.oncf.gare_app.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UtilisateurSystemeRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginResponse authenticate(LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getNomUtilisateur(),
                            loginRequest.getMotDePasse()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Get user entity
            UtilisateurSysteme utilisateur = userDetailsService.findByUsername(userDetails.getUsername());

            // Update last connection
            utilisateur.setDerniereConnexion(LocalDate.now());
            utilisateurRepository.save(utilisateur);

            // Generate JWT token with user info
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", utilisateur.getId());
            extraClaims.put("role", utilisateur.getRole().name());

            String token = jwtUtil.generateToken(extraClaims, userDetails);

            // Build response
            return LoginResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .id(utilisateur.getId())
                    .matricule(utilisateur.getMatricule())
                    .nomPrenom(utilisateur.getNomPrenom())
                    .nomUtilisateur(utilisateur.getNomUtilisateur())
                    .email(utilisateur.getEmail())
                    .role(utilisateur.getRole())
                    .actif(utilisateur.isActif())
                    .actId(utilisateur.getAct() != null ? utilisateur.getAct().getId() : null)
                    .actNomPrenom(utilisateur.getAct() != null ? utilisateur.getAct().getNomPrenom() : null)
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Failed authentication attempt for user: {}", loginRequest.getNomUtilisateur());
            throw new BadRequestException("Nom d'utilisateur ou mot de passe incorrect");
        } catch (DisabledException e) {
            log.warn("Disabled user tried to login: {}", loginRequest.getNomUtilisateur());
            throw new BadRequestException("Compte d'utilisateur désactivé");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponse getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UtilisateurSysteme utilisateur = utilisateurRepository
                .findByNomUtilisateur(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + username));

        return utilisateurMapper.toDto(utilisateur);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UtilisateurSysteme utilisateur = utilisateurRepository
                .findByNomUtilisateur(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + username));

        // Verify old password
        if (!passwordEncoder.matches(changePasswordRequest.getAncienMotDePasse(), utilisateur.getMotDePasseHash())) {
            throw new BadRequestException("Ancien mot de passe incorrect");
        }

        // Update password
        utilisateur.setMotDePasseHash(passwordEncoder.encode(changePasswordRequest.getNouveauMotDePasse()));
        utilisateurRepository.save(utilisateur);

        log.info("Password changed for user: {}", username);
    }

    @Override
    public void logout() {
        // Clear security context
        SecurityContextHolder.clearContext();
        log.info("User logged out successfully");
    }
}