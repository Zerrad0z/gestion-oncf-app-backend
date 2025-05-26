package com.oncf.gare_app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "lettre_sommation_billet_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LettreSommationBilletAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lettre_id", nullable = false)
    private LettreSommationBillet lettre;

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