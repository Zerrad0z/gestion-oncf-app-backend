package com.oncf.gare_app.controller;

import com.oncf.gare_app.dto.BulkUpdateStatusRequest;
import com.oncf.gare_app.dto.LettreSommationBilletRequest;
import com.oncf.gare_app.dto.LettreSommationBilletResponse;
import com.oncf.gare_app.enums.StatutEnum;
import com.oncf.gare_app.service.LettreSommationBilletService;
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
@RequestMapping("/api/v1/lettres-sommation-billet")
@RequiredArgsConstructor
public class LettreSommationBilletController {

    private final LettreSommationBilletService lettreSommationBilletService;

    @GetMapping
    public ResponseEntity<List<LettreSommationBilletResponse>> getAllLettresSommationBillet() {
        return ResponseEntity.ok(lettreSommationBilletService.getAllLettresSommationBillet());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LettreSommationBilletResponse> getLettreSommationBilletById(@PathVariable Long id) {
        return ResponseEntity.ok(lettreSommationBilletService.getLettreSommationBilletById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LettreSommationBilletResponse> createLettreSommationBillet(
            @Valid @RequestPart("lettre") LettreSommationBilletRequest request,
            @RequestPart(value = "fichiers", required = false) List<MultipartFile> fichiers) {

        return new ResponseEntity<>(
                lettreSommationBilletService.createLettreSommationBillet(request, fichiers),
                HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LettreSommationBilletResponse> updateLettreSommationBillet(
            @PathVariable Long id,
            @Valid @RequestPart("lettre") LettreSommationBilletRequest request,
            @RequestPart(value = "fichiers", required = false) List<MultipartFile> fichiers) {

        return ResponseEntity.ok(
                lettreSommationBilletService.updateLettreSommationBillet(id, request, fichiers));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLettreSommationBillet(@PathVariable Long id) {
        lettreSommationBilletService.deleteLettreSommationBillet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<LettreSommationBilletResponse>> searchLettresSommationBillet(
            @RequestParam(required = false) Long actId,
            @RequestParam(required = false) Long gareId,
            @RequestParam(required = false) Long trainId,
            @RequestParam(required = false) StatutEnum statut,
            @RequestParam(required = false) String numeroBillet,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        return ResponseEntity.ok(lettreSommationBilletService.searchLettresSommationBillet(
                actId, gareId, trainId, statut, numeroBillet, dateDebut, dateFin));
    }

    @GetMapping("/act/{actId}")
    public ResponseEntity<List<LettreSommationBilletResponse>> getLettreSommationBilletByActId(@PathVariable Long actId) {
        return ResponseEntity.ok(lettreSommationBilletService.getLettreSommationBilletByActId(actId));
    }

    @GetMapping("/gare/{gareId}")
    public ResponseEntity<List<LettreSommationBilletResponse>> getLettreSommationBilletByGareId(@PathVariable Long gareId) {
        return ResponseEntity.ok(lettreSommationBilletService.getLettreSommationBilletByGareId(gareId));
    }

    @GetMapping("/train/{trainId}")
    public ResponseEntity<List<LettreSommationBilletResponse>> getLettreSommationBilletByTrainId(@PathVariable Long trainId) {
        return ResponseEntity.ok(lettreSommationBilletService.getLettreSommationBilletByTrainId(trainId));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<LettreSommationBilletResponse>> getLettreSommationBilletByStatut(@PathVariable StatutEnum statut) {
        return ResponseEntity.ok(lettreSommationBilletService.getLettreSommationBilletByStatut(statut));
    }

    @GetMapping("/dates")
    public ResponseEntity<List<LettreSommationBilletResponse>> getLettreSommationBilletByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        return ResponseEntity.ok(lettreSommationBilletService.getLettreSommationBilletByDateRange(dateDebut, dateFin));
    }

    @GetMapping("/check-numero-billet")
    public ResponseEntity<Boolean> existsLettreSommationBilletByNumeroBillet(@RequestParam String numeroBillet) {
        return ResponseEntity.ok(lettreSommationBilletService.existsLettreSommationBilletByNumeroBillet(numeroBillet));
    }

    @PutMapping("/bulk/status")
    public ResponseEntity<List<LettreSommationBilletResponse>> updateBulkStatus(
            @Valid @RequestBody BulkUpdateStatusRequest request) {
        return ResponseEntity.ok(lettreSommationBilletService.updateBulkStatus(request));
    }
}