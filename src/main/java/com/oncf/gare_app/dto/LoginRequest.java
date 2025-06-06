package com.oncf.gare_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String nomUtilisateur;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;
}
