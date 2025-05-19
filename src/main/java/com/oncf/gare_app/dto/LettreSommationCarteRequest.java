package com.oncf.gare_app.dto;

import com.oncf.gare_app.enums.StatutEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LettreSommationCarteRequest {
    @NotNull(message = "L'ID de l'agent ACT est obligatoire")
    private Long actId;

    @NotNull(message = "L'ID du train est obligatoire")
    private Long trainId;

    @NotNull(message = "L'ID de la gare est obligatoire")
    private Long gareId;

    @NotNull(message = "La date de création est obligatoire")
    private LocalDate dateCreation;

    @NotNull(message = "La date d'infraction est obligatoire")
    private LocalDate dateInfraction;

    @NotNull(message = "Le statut est obligatoire")
    private StatutEnum statut;

    private String gareReglement;

    private String numeroPpRegularisation;

    @NotBlank(message = "Le montant de l'amende est obligatoire")
    @Size(max = 50, message = "Le montant de l'amende ne doit pas dépasser 50 caractères")
    private String montantAmende;

    @NotBlank(message = "Le type de carte est obligatoire")
    @Size(max = 50, message = "Le type de carte ne doit pas dépasser 50 caractères")
    private String typeCarte;

    @NotBlank(message = "Le numéro de carte est obligatoire")
    @Size(max = 50, message = "Le numéro de carte ne doit pas dépasser 50 caractères")
    private String numeroCarte;

    private String commentaires;

    private LocalDate dateTraitement;

    private List<MultipartFile> fichiers;
}