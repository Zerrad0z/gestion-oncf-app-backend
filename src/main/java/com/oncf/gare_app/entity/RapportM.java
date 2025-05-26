package com.oncf.gare_app.entity;

import com.oncf.gare_app.enums.CategorieRapportEnum;
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
@Table(name = "RAPPORT_M")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RapportM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "act_id", nullable = false)
    private ACT act;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieRapportEnum categorie;

    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Column(name = "priorite")
    private Integer priorite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEnum statut;

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

    // Make this transient - loaded separately by mapper
    @Transient
    private List<PieceJointe> piecesJointes = new ArrayList<>();

    public void addPieceJointe(PieceJointe pieceJointe) {
        if (this.piecesJointes == null) {
            this.piecesJointes = new ArrayList<>();
        }
        pieceJointe.setTypeDocument(TypeDocumentEnum.RAPPORT_M);
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