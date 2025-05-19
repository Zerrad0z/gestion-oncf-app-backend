package com.oncf.gare_app.service;

import com.oncf.gare_app.dto.DocumentStatisticsResponse;
import com.oncf.gare_app.enums.TypeDocumentEnum;

import java.time.LocalDate;

public interface DocumentStatisticsService {

    /**
     * Get statistics for all documents
     */
    DocumentStatisticsResponse getOverallStatistics();

    /**
     * Get statistics for a specific document type
     */
    DocumentStatisticsResponse getStatisticsByDocumentType(TypeDocumentEnum typeDocument);

    /**
     * Get statistics for a date range
     */
    DocumentStatisticsResponse getStatisticsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Get statistics for a specific document
     */
    DocumentStatisticsResponse getStatisticsForDocument(TypeDocumentEnum typeDocument, Long documentId);
}