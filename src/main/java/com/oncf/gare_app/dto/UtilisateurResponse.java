package com.oncf.gare_app.dto;

import com.oncf.gare_app.enums.RoleUtilisateur;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurResponse {
    private Long id;
    private String matricule;
    private String nomPrenom;
    private String nomUtilisateur;
    private String email;
    private RoleUtilisateur role;
    private LocalDate derniereConnexion;
    private boolean actif;
    private Long actId;
    private String actNomPrenom;
    private LocalDateTime dateCreation;
    private LocalDateTime dateDerniereModification;
}