package com.oncf.gare_app.entity;

import com.oncf.gare_app.enums.TypeDocumentEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "piece_jointe")
@Data
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

    @Column(name = "type_mime")
    private String typeMime;

    @Column(name = "taille")
    private Long taille;

    @CreationTimestamp
    @Column(name = "date_upload", nullable = false)
    private LocalDateTime dateUpload;

    @PrePersist
    public void prePersist() {
        if (dateUpload == null) {
            dateUpload = LocalDateTime.now();
        }
    }
}