package com.oncf.gare_app.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
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