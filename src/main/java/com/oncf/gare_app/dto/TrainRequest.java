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
public class TrainRequest {
    @NotBlank(message = "Le numéro du train est obligatoire")
    @Size(min = 1, max = 50, message = "Le numéro du train doit contenir entre 1 et 50 caractères")
    private String numero;
}