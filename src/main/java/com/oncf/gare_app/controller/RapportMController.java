package com.oncf.gare_app.controller;

import com.oncf.gare_app.dto.BulkUpdateStatusRequest;
import com.oncf.gare_app.dto.RapportMRequest;
import com.oncf.gare_app.dto.RapportMResponse;
import com.oncf.gare_app.enums.CategorieRapportEnum;
import com.oncf.gare_app.service.RapportMService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rapports-m")
@RequiredArgsConstructor
public class RapportMController {

    private final RapportMService rapportMService;

    @GetMapping
    public ResponseEntity<List<RapportMResponse>> getAllRapportsM() {
        return ResponseEntity.ok(rapportMService.getAllRapportsM());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RapportMResponse> getRapportMById(@PathVariable Long id) {
        return ResponseEntity.ok(rapportMService.getRapportMById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RapportMResponse> createRapportM(
            @Valid @RequestPart("rapport") RapportMRequest request,
            @RequestPart(value = "fichiers", required = false) List<MultipartFile> fichiers) {

        return new ResponseEntity<>(
                rapportMService.createRapportM(request, fichiers),
                HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RapportMResponse> updateRapportM(
            @PathVariable Long id,
            @Valid @RequestPart("rapport") RapportMRequest request,
            @RequestPart(value = "fichiers", required = false) List<MultipartFile> fichiers) {

        return ResponseEntity.ok(
                rapportMService.updateRapportM(id, request, fichiers));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRapportM(@PathVariable Long id) {
        rapportMService.deleteRapportM(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<RapportMResponse>> searchRapportsM(
            @RequestParam(required = false) Long actId,
            @RequestParam(required = false) CategorieRapportEnum categorie,
            @RequestParam(required = false) String references,
            @RequestParam(required = false) String objet,
            @RequestParam(required = false) String detail,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        return ResponseEntity.ok(rapportMService.searchRapportsM(
                actId, categorie, references, objet, detail, dateDebut, dateFin));
    }

    @GetMapping("/act/{actId}")
    public ResponseEntity<List<RapportMResponse>> getRapportMByActId(@PathVariable Long actId) {
        return ResponseEntity.ok(rapportMService.getRapportMByActId(actId));
    }

    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<List<RapportMResponse>> getRapportMByCategorie(@PathVariable CategorieRapportEnum categorie) {
        return ResponseEntity.ok(rapportMService.getRapportMByCategorie(categorie));
    }

    @GetMapping("/dates")
    public ResponseEntity<List<RapportMResponse>> getRapportMByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        return ResponseEntity.ok(rapportMService.getRapportMByDateRange(dateDebut, dateFin));
    }

    @PutMapping("/bulk")
    public ResponseEntity<List<RapportMResponse>> updateBulk(
            @Valid @RequestBody BulkUpdateStatusRequest request) {
        return ResponseEntity.ok(rapportMService.updateBulk(request));
    }
}