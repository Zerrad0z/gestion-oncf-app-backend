package com.oncf.gare_app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_type", nullable = false)
    private String documentType;  // Use the enum name as a string

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "chemin_fichier", nullable = false)
    private String cheminFichier;

    @Column(name = "type_mime")
    private String typeMime;

    @Column(nullable = false)
    private Long taille;

    @CreationTimestamp
    @Column(name = "date_upload", updatable = false)
    private LocalDateTime dateUpload;
}