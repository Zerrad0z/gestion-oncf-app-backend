package com.oncf.gare_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GareRequest {
    @NotBlank(message = "Le nom de la gare est obligatoire")
    @Size(min = 1, max = 100, message = "Le nom de la gare doit contenir entre 1 et 100 caractères")
    private String nom;
}