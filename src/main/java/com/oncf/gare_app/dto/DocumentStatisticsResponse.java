package com.oncf.gare_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentStatisticsResponse {
    // Document counts
    private long totalDocumentCount;
    private Map<String, Long> documentCountsByType;

    // File counts
    private long totalFileCount;
    private Map<String, Long> fileCountsByDocumentType;

    // Storage statistics
    private long totalStorageSize;
    private String totalStorageSizeFormatted;
    private Map<String, Long> storageSizeByDocumentType;
    private Map<String, String> storageSizeByDocumentTypeFormatted;

    // Status statistics
    private Map<String, Long> statusDistribution;

    // Time-based statistics
    private Map<String, Long> documentsByMonth;
    private Map<String, Long> documentsByYear;
}