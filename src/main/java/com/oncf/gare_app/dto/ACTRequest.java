package com.oncf.gare_app.dto;

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
public class ACTRequest {
    @NotBlank(message = "Le matricule est obligatoire")
    @Size(min = 1, max = 50, message = "Le matricule doit contenir entre 1 et 50 caractères")
    private String matricule;

    @NotBlank(message = "Le nom et prénom sont obligatoires")
    @Size(min = 1, max = 100, message = "Le nom et prénom doivent contenir entre 1 et 100 caractères")
    private String nomPrenom;

    @NotNull(message = "L'ID de l'antenne est obligatoire")
    private Long antenneId;
}