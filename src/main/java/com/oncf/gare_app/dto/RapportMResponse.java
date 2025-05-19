package com.oncf.gare_app.dto;

import com.oncf.gare_app.enums.CategorieRapportEnum;
import com.oncf.gare_app.enums.StatutEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RapportMResponse {
    private Long id;
    private ACTResponse act;
    private CategorieRapportEnum categorie;
    private LocalDate dateCreation;
    private String titre;
    private String contenu;
    private Integer priorite;
    private StatutEnum statut;
    private LocalDate dateTraitement;
    private UtilisateurResponse utilisateur;
    private LocalDateTime dateCreationSysteme;
    private LocalDateTime dateDerniereModification;
    private List<PieceJointeResponse> piecesJointes;
}