package com.oncf.gare_app.dto;

import com.oncf.gare_app.enums.CategorieRapportEnum;
import com.oncf.gare_app.enums.StatutEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RapportMRequest {
    @NotNull(message = "L'ID de l'agent ACT est obligatoire")
    private Long actId;

    @NotNull(message = "L'ID du train est obligatoire")
    private Long trainId;

    @NotBlank(message = "Les références sont obligatoires")
    private String references;

    @NotNull(message = "La date d'envoi est obligatoire")
    private LocalDate dateEnvoi;

    private LocalDate dateReception;

    @NotBlank(message = "L'objet est obligatoire")
    private String objet;

    @NotNull(message = "La catégorie est obligatoire")
    private CategorieRapportEnum categorie;

    @NotBlank(message = "Le détail est obligatoire")
    private String detail;

    @NotNull(message = "La date du train est obligatoire")
    private LocalDate dateTrain;
}