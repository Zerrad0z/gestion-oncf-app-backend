package com.oncf.gare_app.entity;

import com.oncf.gare_app.enums.TypeDocumentEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "PIECE_JOINTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceJointe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_document", nullable = false)
    private TypeDocumentEnum typeDocument;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "chemin_fichier", nullable = false)
    private String cheminFichier;

    @Column(name = "type_mime", nullable = false)
    private String typeMime;

    @Column(nullable = false)
    private Long taille;

    @CreationTimestamp
    @Column(name = "date_upload", updatable = false)
    private LocalDateTime dateUpload;
}