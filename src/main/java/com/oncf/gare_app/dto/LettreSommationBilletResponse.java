package com.oncf.gare_app.dto;

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
public class LettreSommationBilletResponse {
    private Long id;
    private ACTResponse act;
    private TrainResponse train;
    private GareResponse gare;
    private LocalDate dateCreation;
    private LocalDate dateInfraction;
    private StatutEnum statut;
    private String montantAmende;
    private String motifInfraction;
    private String numeroBillet;
    private String commentaires;
    private LocalDate dateTraitement;
    private UtilisateurResponse utilisateur;
    private LocalDateTime dateCreationSysteme;
    private LocalDateTime dateDerniereModification;
    private List<PieceJointeResponse> piecesJointes;
}