package com.oncf.gare_app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ACT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ACT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String matricule;

    @Column(name = "nom_prenom", nullable = false)
    private String nomPrenom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "antenne_id", nullable = false)
    private Antenne antenne;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_derniere_modification")
    private LocalDateTime dateDerniereModification;

//    @OneToMany(mappedBy = "act", cascade = CascadeType.ALL)
//    private List<LettreSommationBillet> lettresSommationBillet = new ArrayList<>();
//
//    @OneToMany(mappedBy = "act", cascade = CascadeType.ALL)
//    private List<LettreSommationCarte> lettresSommationCarte = new ArrayList<>();
//
//    @OneToMany(mappedBy = "act", cascade = CascadeType.ALL)
//    private List<RapportM> rapportsM = new ArrayList<>();
//
//    @OneToOne(mappedBy = "act")
//    private UtilisateurSysteme utilisateurSysteme;
}