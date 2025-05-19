package com.oncf.gare_app.dto;

import com.oncf.gare_app.enums.RoleUtilisateur;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurRequest {
    @NotBlank(message = "Le matricule est obligatoire")
    @Size(min = 2, max = 20, message = "Le matricule doit contenir entre 2 et 20 caractères")
    private String matricule;

    @NotBlank(message = "Le nom et prénom sont obligatoires")
    @Size(min = 2, max = 100, message = "Le nom et prénom doivent contenir entre 2 et 100 caractères")
    private String nomPrenom;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String nomUtilisateur;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email n'est pas valide")
    private String email;

    @NotNull(message = "Le rôle est obligatoire")
    private RoleUtilisateur role;

    private boolean actif = true;

    private Long actId;
}