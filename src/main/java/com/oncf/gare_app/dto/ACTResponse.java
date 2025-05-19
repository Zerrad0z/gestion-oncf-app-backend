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
public class ACTResponse {
    private Long id;
    private String matricule;
    private String nomPrenom;
    private AntenneResponseDto antenne;
    private LocalDateTime dateCreation;
    private LocalDateTime dateDerniereModification;
}