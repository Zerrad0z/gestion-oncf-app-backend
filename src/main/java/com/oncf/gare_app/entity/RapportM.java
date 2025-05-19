package com.oncf.gare_app.entity;

import com.oncf.gare_app.enums.CategorieRapportEnum;
import com.oncf.gare_app.enums.StatutEnum;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    @Where(clause = "type_document = 'RAPPORT_M'")
    private List<PieceJointe> piecesJointes = new ArrayList<>();

    /**
     * Helper method to add a piece jointe to this document
     */
    public void addPieceJointe(PieceJointe pieceJointe) {
        pieceJointe.setTypeDocument(TypeDocumentEnum.RAPPORT_M);
        pieceJointe.setDocumentId(this.id);
        this.piecesJointes.add(pieceJointe);
    }

    /**
     * Helper method to remove a piece jointe from this document
     */
    public void removePieceJointe(PieceJointe pieceJointe) {
        this.piecesJointes.remove(pieceJointe);
    }

    /**
     * Helper method to clear all pieces jointes from this document
     */
    public void clearPiecesJointes() {
        this.piecesJointes.clear();
    }
}