package com.oncf.gare_app.controller;

import com.oncf.gare_app.dto.DocumentStatisticsResponse;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.service.DocumentStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final DocumentStatisticsService documentStatisticsService;

    @GetMapping
    public ResponseEntity<DocumentStatisticsResponse> getOverallStatistics() {
        return ResponseEntity.ok(documentStatisticsService.getOverallStatistics());
    }

    @GetMapping("/by-type/{typeDocument}")
    public ResponseEntity<DocumentStatisticsResponse> getStatisticsByDocumentType(
            @PathVariable TypeDocumentEnum typeDocument) {
        return ResponseEntity.ok(documentStatisticsService.getStatisticsByDocumentType(typeDocument));
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<DocumentStatisticsResponse> getStatisticsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(documentStatisticsService.getStatisticsByDateRange(startDate, endDate));
    }

    @GetMapping("/document/{typeDocument}/{documentId}")
    public ResponseEntity<DocumentStatisticsResponse> getStatisticsForDocument(
            @PathVariable TypeDocumentEnum typeDocument,
            @PathVariable Long documentId) {
        return ResponseEntity.ok(documentStatisticsService.getStatisticsForDocument(typeDocument, documentId));
    }
}