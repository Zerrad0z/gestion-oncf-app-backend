package com.oncf.gare_app.dto;

import com.oncf.gare_app.enums.RoleUtilisateur;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String matricule;
    private String nomPrenom;
    private String nomUtilisateur;
    private String email;
    private RoleUtilisateur role;
    private boolean actif;
    private Long actId;
    private String actNomPrenom;
}
