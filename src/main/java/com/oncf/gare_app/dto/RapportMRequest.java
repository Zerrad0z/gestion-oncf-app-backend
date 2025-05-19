package com.oncf.gare_app.dto;

import com.oncf.gare_app.enums.CategorieRapportEnum;
import com.oncf.gare_app.enums.StatutEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class RapportMRequest {
    @NotNull(message = "L'ID de l'agent ACT est obligatoire")
    private Long actId;

    @NotNull(message = "La catégorie du rapport est obligatoire")
    private CategorieRapportEnum categorie;

    @NotNull(message = "La date de création est obligatoire")
    private LocalDate dateCreation;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    private Integer priorite;

    @NotNull(message = "Le statut est obligatoire")
    private StatutEnum statut;

    private LocalDate dateTraitement;

    private List<MultipartFile> fichiers;
}