package com.oncf.gare_app.dto;

import com.oncf.gare_app.enums.RoleUtilisateur;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurUpdateRequest {
    @Size(min = 2, max = 20, message = "Le matricule doit contenir entre 2 et 20 caractères")
    private String matricule;

    @Size(min = 2, max = 100, message = "Le nom et prénom doivent contenir entre 2 et 100 caractères")
    private String nomPrenom;

    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String nomUtilisateur;

    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;

    @Email(message = "L'email n'est pas valide")
    private String email;

    private RoleUtilisateur role;

    private Boolean actif;

    private Long actId;
}