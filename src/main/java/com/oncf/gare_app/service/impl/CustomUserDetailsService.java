package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.repository.UtilisateurSystemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurSystemeRepository utilisateurRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UtilisateurSysteme utilisateur = utilisateurRepository
                .findByNomUtilisateur(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));

        if (!utilisateur.isActif()) {
            throw new UsernameNotFoundException("Utilisateur désactivé: " + username);
        }

        return User.builder()
                .username(utilisateur.getNomUtilisateur())
                .password(utilisateur.getMotDePasseHash())
                .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name())))
                .accountExpired(false)
                .accountLocked(!utilisateur.isActif())
                .credentialsExpired(false)
                .disabled(!utilisateur.isActif())
                .build();
    }

    @Transactional(readOnly = true)
    public UtilisateurSysteme findByUsername(String username) {
        return utilisateurRepository
                .findByNomUtilisateur(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));
    }
}