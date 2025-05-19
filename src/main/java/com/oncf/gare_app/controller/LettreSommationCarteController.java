package com.oncf.gare_app.controller;

import com.oncf.gare_app.dto.BulkUpdateStatusRequest;
import com.oncf.gare_app.dto.LettreSommationCarteRequest;
import com.oncf.gare_app.dto.LettreSommationCarteResponse;
import com.oncf.gare_app.enums.StatutEnum;
import com.oncf.gare_app.service.LettreSommationCarteService;
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
@RequestMapping("/api/v1/lettres-sommation-carte")
@RequiredArgsConstructor
public class LettreSommationCarteController {

    private final LettreSommationCarteService lettreSommationCarteService;

    @GetMapping
    public ResponseEntity<List<LettreSommationCarteResponse>> getAllLettresSommationCarte() {
        return ResponseEntity.ok(lettreSommationCarteService.getAllLettresSommationCarte());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LettreSommationCarteResponse> getLettreSommationCarteById(@PathVariable Long id) {
        return ResponseEntity.ok(lettreSommationCarteService.getLettreSommationCarteById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LettreSommationCarteResponse> createLettreSommationCarte(
            @Valid @RequestPart("lettre") LettreSommationCarteRequest request,
            @RequestPart(value = "fichiers", required = false) List<MultipartFile> fichiers) {

        return new ResponseEntity<>(
                lettreSommationCarteService.createLettreSommationCarte(request, fichiers),
                HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LettreSommationCarteResponse> updateLettreSommationCarte(
            @PathVariable Long id,
            @Valid @RequestPart("lettre") LettreSommationCarteRequest request,
            @RequestPart(value = "fichiers", required = false) List<MultipartFile> fichiers) {

        return ResponseEntity.ok(
                lettreSommationCarteService.updateLettreSommationCarte(id, request, fichiers));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLettreSommationCarte(@PathVariable Long id) {
        lettreSommationCarteService.deleteLettreSommationCarte(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<LettreSommationCarteResponse>> searchLettresSommationCarte(
            @RequestParam(required = false) Long actId,
            @RequestParam(required = false) Long gareId,
            @RequestParam(required = false) Long trainId,
            @RequestParam(required = false) StatutEnum statut,
            @RequestParam(required = false) String numeroCarte,
            @RequestParam(required = false) String typeCarte,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        return ResponseEntity.ok(lettreSommationCarteService.searchLettresSommationCarte(
                actId, gareId, trainId, statut, numeroCarte, typeCarte, dateDebut, dateFin));
    }

    @GetMapping("/act/{actId}")
    public ResponseEntity<List<LettreSommationCarteResponse>> getLettreSommationCarteByActId(@PathVariable Long actId) {
        return ResponseEntity.ok(lettreSommationCarteService.getLettreSommationCarteByActId(actId));
    }

    @GetMapping("/gare/{gareId}")
    public ResponseEntity<List<LettreSommationCarteResponse>> getLettreSommationCarteByGareId(@PathVariable Long gareId) {
        return ResponseEntity.ok(lettreSommationCarteService.getLettreSommationCarteByGareId(gareId));
    }

    @GetMapping("/train/{trainId}")
    public ResponseEntity<List<LettreSommationCarteResponse>> getLettreSommationCarteByTrainId(@PathVariable Long trainId) {
        return ResponseEntity.ok(lettreSommationCarteService.getLettreSommationCarteByTrainId(trainId));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<LettreSommationCarteResponse>> getLettreSommationCarteByStatut(@PathVariable StatutEnum statut) {
        return ResponseEntity.ok(lettreSommationCarteService.getLettreSommationCarteByStatut(statut));
    }

    @GetMapping("/dates")
    public ResponseEntity<List<LettreSommationCarteResponse>> getLettreSommationCarteByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        return ResponseEntity.ok(lettreSommationCarteService.getLettreSommationCarteByDateRange(dateDebut, dateFin));
    }

    @GetMapping("/check-numero-carte")
    public ResponseEntity<Boolean> existsLettreSommationCarteByNumeroCarte(@RequestParam String numeroCarte) {
        return ResponseEntity.ok(lettreSommationCarteService.existsLettreSommationCarteByNumeroCarte(numeroCarte));
    }

    @PutMapping("/bulk/status")
    public ResponseEntity<List<LettreSommationCarteResponse>> updateBulkStatus(
            @Valid @RequestBody BulkUpdateStatusRequest request) {
        return ResponseEntity.ok(lettreSommationCarteService.updateBulkStatus(request));
    }
}