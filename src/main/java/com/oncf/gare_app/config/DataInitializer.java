package com.oncf.gare_app.config;

import com.oncf.gare_app.entity.UtilisateurSysteme;
import com.oncf.gare_app.enums.RoleUtilisateur;
import com.oncf.gare_app.repository.UtilisateurSystemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurSystemeRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createTestUsers();
    }

    private void createTestUsers() {
        // Check if users already exist
        if (utilisateurRepository.count() > 0) {
            log.info("Users already exist. Skipping initialization.");
            return;
        }

        log.info("Creating test users...");

        // Create Admin User
        UtilisateurSysteme admin = UtilisateurSysteme.builder()
                .matricule("ADMIN001")
                .nomPrenom("Administrateur ONCF")
                .nomUtilisateur("admin")
                .motDePasseHash(passwordEncoder.encode("admin123"))
                .email("admin@oncf.ma")
                .role(RoleUtilisateur.ADMIN)
                .actif(true)
                .build();

        utilisateurRepository.save(admin);
        log.info("Created admin user: {}", admin.getNomUtilisateur());

        // Create Superviseur User
        UtilisateurSysteme superviseur = UtilisateurSysteme.builder()
                .matricule("SUP001")
                .nomPrenom("Mohammed Alami")
                .nomUtilisateur("super")
                .motDePasseHash(passwordEncoder.encode("super123"))
                .email("m.alami@oncf.ma")
                .role(RoleUtilisateur.SUPERVISEUR)
                .actif(true)
                .build();

        utilisateurRepository.save(superviseur);
        log.info("Created superviseur user: {}", superviseur.getNomUtilisateur());

        // Create Encadrant User
        UtilisateurSysteme encadrant = UtilisateurSysteme.builder()
                .matricule("ENC001")
                .nomPrenom("Fatima Zahra")
                .nomUtilisateur("encad")
                .motDePasseHash(passwordEncoder.encode("encad123"))
                .email("f.zahra@oncf.ma")
                .role(RoleUtilisateur.ENCADRANT)
                .actif(true)
                .build();

        utilisateurRepository.save(encadrant);
        log.info("Created encadrant user: {}", encadrant.getNomUtilisateur());

        // Create additional test users
        createAdditionalTestUsers();

        log.info("Test users created successfully!");
    }

    private void createAdditionalTestUsers() {
        // Create more superviseurs
        UtilisateurSysteme sup2 = UtilisateurSysteme.builder()
                .matricule("SUP002")
                .nomPrenom("Ahmed Benali")
                .nomUtilisateur("a.benali")
                .motDePasseHash(passwordEncoder.encode("test123"))
                .email("a.benali@oncf.ma")
                .role(RoleUtilisateur.SUPERVISEUR)
                .actif(true)
                .build();
        utilisateurRepository.save(sup2);

        // Create more encadrants
        UtilisateurSysteme enc2 = UtilisateurSysteme.builder()
                .matricule("ENC002")
                .nomPrenom("Youssef Kabbaj")
                .nomUtilisateur("y.kabbaj")
                .motDePasseHash(passwordEncoder.encode("test123"))
                .email("y.kabbaj@oncf.ma")
                .role(RoleUtilisateur.ENCADRANT)
                .actif(true)
                .build();
        utilisateurRepository.save(enc2);

        UtilisateurSysteme enc3 = UtilisateurSysteme.builder()
                .matricule("ENC003")
                .nomPrenom("Aicha Tazi")
                .nomUtilisateur("a.tazi")
                .motDePasseHash(passwordEncoder.encode("test123"))
                .email("a.tazi@oncf.ma")
                .role(RoleUtilisateur.ENCADRANT)
                .actif(false) // Inactive user for testing
                .build();
        utilisateurRepository.save(enc3);

        log.info("Additional test users created");
    }
}