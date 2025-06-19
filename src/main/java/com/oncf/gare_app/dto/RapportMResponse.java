package com.oncf.gare_app.dto;

import com.oncf.gare_app.enums.CategorieRapportEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RapportMResponse {
    private Long id;
    private String references;
    private LocalDate dateEnvoi;
    private LocalDate dateReception;
    private String objet;
    private CategorieRapportEnum categorie;
    private String detail;
    private ACTResponse act;
    private TrainResponse train;
    private LocalDate dateTrain;
    private UtilisateurResponse utilisateur;
    private LocalDateTime dateCreationSysteme;
    private LocalDateTime dateDerniereModification;
    private List<PieceJointeResponse> piecesJointes;
}