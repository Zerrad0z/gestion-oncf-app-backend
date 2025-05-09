package com.oncf.gare_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AntenneResponseDto {

    private Long id;
    private String nom;
    private Long sectionId;
    private String sectionNom;
    private LocalDateTime dateCreation;
    private LocalDateTime dateDerniereModification;
    private int nombreAgents;
}