package com.oncf.gare_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GareResponse {
    private Long id;
    private String nom;
    private LocalDateTime dateCreation;
    private LocalDateTime dateDerniereModification;
}

