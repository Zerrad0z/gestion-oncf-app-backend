package com.oncf.gare_app.entity;

import com.oncf.gare_app.enums.StatutEnum;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LETTRE_SOMMATION_CARTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LettreSommationCarte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "act_id", nullable = false)
    private ACT act;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gare_id", nullable = false)
    private Gare gare;

    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;

    @Column(name = "date_infraction", nullable = false)
    private LocalDate dateInfraction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEnum statut;

    @Column(name = "gare_reglement")
    private String gareReglement;

    @Column(name = "numero_pp_regularisation")
    private String numeroPpRegularisation;

    @Column(name = "montant_amende", nullable = false)
    private String montantAmende;

    @Column(name = "type_carte", nullable = false)
    private String typeCarte;

    @Column(name = "numero_carte", nullable = false)
    private String numeroCarte;

    @Column(columnDefinition = "TEXT")
    private String commentaires;

    @Column(name = "date_traitement")
    private LocalDate dateTraitement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private UtilisateurSysteme utilisateur;

    @CreationTimestamp
    @Column(name = "date_creation_systeme", updatable = false)
    private LocalDateTime dateCreationSysteme;

    @UpdateTimestamp
    @Column(name = "date_derniere_modification")
    private LocalDateTime dateDerniereModification;

    @Transient
    private List<PieceJointe> piecesJointes = new ArrayList<>();

    public void addPieceJointe(PieceJointe pieceJointe) {
        if (this.piecesJointes == null) {
            this.piecesJointes = new ArrayList<>();
        }
        pieceJointe.setTypeDocument(TypeDocumentEnum.LETTRE_CARTE);
        if (this.id != null) {
            pieceJointe.setDocumentId(this.id);
        }
        this.piecesJointes.add(pieceJointe);
    }

    public void removePieceJointe(PieceJointe pieceJointe) {
        if (this.piecesJointes != null) {
            this.piecesJointes.remove(pieceJointe);
        }
    }

    public void clearPiecesJointes() {
        if (this.piecesJointes != null) {
            this.piecesJointes.clear();
        }
    }
}
