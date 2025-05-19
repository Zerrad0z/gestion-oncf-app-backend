package com.oncf.gare_app.controller;

import com.oncf.gare_app.dto.HistoriqueTraitementResponse;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.service.HistoriqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/historique")
@RequiredArgsConstructor
public class HistoriqueController {

    private final HistoriqueService historiqueService;

    @GetMapping("/document/{type}/{id}")
    public ResponseEntity<List<HistoriqueTraitementResponse>> getHistoriqueForDocument(
            @PathVariable("type") TypeDocumentEnum typeDocument,
            @PathVariable("id") Long documentId) {
        return ResponseEntity.ok(historiqueService.getHistoriqueForDocument(typeDocument, documentId));
    }

    @GetMapping("/utilisateur/{id}")
    public ResponseEntity<List<HistoriqueTraitementResponse>> getHistoriqueForUtilisateur(
            @PathVariable("id") Long utilisateurId) {
        return ResponseEntity.ok(historiqueService.getHistoriqueForUtilisateur(utilisateurId));
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<HistoriqueTraitementResponse>> getHistoriqueForAction(
            @PathVariable("action") String action) {
        return ResponseEntity.ok(historiqueService.getHistoriqueForAction(action));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<HistoriqueTraitementResponse>> searchHistorique(
            @RequestParam(required = false) TypeDocumentEnum typeDocument,
            @RequestParam(required = false) Long documentId,
            @RequestParam(required = false) Long utilisateurId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            Pageable pageable) {

        return ResponseEntity.ok(historiqueService.searchHistorique(
                typeDocument, documentId, utilisateurId, action, dateDebut, dateFin, pageable));
    }
}