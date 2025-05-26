package com.oncf.gare_app.entity;

import com.oncf.gare_app.enums.StatutEnum;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LETTRE_SOMMATION_BILLET")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LettreSommationBillet {

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

    @Column(name = "montant_amende", nullable = false)
    private String montantAmende;

    @Column(name = "motif_infraction", nullable = false)
    private String motifInfraction;

    @Column(name = "numero_billet", nullable = false)
    private String numeroBillet;

    @Column(columnDefinition = "TEXT")
    private String commentaires;

    @Column(name = "date_traitement")
    private LocalDate dateTraitement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = true)
    private UtilisateurSysteme utilisateur;

    @CreationTimestamp
    @Column(name = "date_creation_systeme", updatable = false)
    private LocalDateTime dateCreationSysteme;

    @UpdateTimestamp
    @Column(name = "date_derniere_modification")
    private LocalDateTime dateDerniereModification;

    // Make this transient - loaded separately by mapper
    @Transient
    @Builder.Default
    private List<PieceJointe> piecesJointes = new ArrayList<>();

    // Keep helper methods for convenience
    public void addPieceJointe(PieceJointe pieceJointe) {
        if (this.piecesJointes == null) {
            this.piecesJointes = new ArrayList<>();
        }
        pieceJointe.setTypeDocument(TypeDocumentEnum.LETTRE_BILLET);
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