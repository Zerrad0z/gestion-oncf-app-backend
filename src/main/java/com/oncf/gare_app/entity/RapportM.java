package com.oncf.gare_app.entity;

import com.oncf.gare_app.enums.CategorieRapportEnum;
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

    @Column(name = "`references`", nullable = false)
    private String references; // Références(numéro)

    @Column(name = "date_envoi", nullable = false)
    private LocalDate dateEnvoi; // Date d'envoi

    @Column(name = "date_reception")
    private LocalDate dateReception; // Date de réception

    @Column(name = "objet", nullable = false)
    private String objet; // Objet (texte)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieRapportEnum categorie; // Catégorie (liste)

    @Column(name = "detail", columnDefinition = "TEXT", nullable = false)
    private String detail; // Détail (texte)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "act_id", nullable = false)
    private ACT act; // ACT information

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train; // Train information

    @Column(name = "date_train", nullable = false)
    private LocalDate dateTrain; // Date du train

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private UtilisateurSysteme utilisateur;

    @CreationTimestamp
    @Column(name = "date_creation_systeme", updatable = false)
    private LocalDateTime dateCreationSysteme;

    @UpdateTimestamp
    @Column(name = "date_derniere_modification")
    private LocalDateTime dateDerniereModification;

    //transient - loaded separately by mapper
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