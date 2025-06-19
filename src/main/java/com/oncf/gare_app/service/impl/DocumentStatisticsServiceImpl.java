package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.dto.DocumentStatisticsResponse;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.repository.LettreSommationBilletRepository;
import com.oncf.gare_app.repository.LettreSommationCarteRepository;
import com.oncf.gare_app.repository.PieceJointeRepository;
import com.oncf.gare_app.repository.RapportMRepository;
import com.oncf.gare_app.service.DocumentStatisticsService;
import com.oncf.gare_app.service.DocumentTypeResolver;
import com.oncf.gare_app.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentStatisticsServiceImpl implements DocumentStatisticsService {

    private final LettreSommationBilletRepository lettreSommationBilletRepository;
    private final LettreSommationCarteRepository lettreSommationCarteRepository;
    private final RapportMRepository rapportMRepository;
    private final PieceJointeRepository pieceJointeRepository;
    private final FileStorageService fileStorageService;
    private final DocumentTypeResolver documentTypeResolver;

    @Override
    @Transactional(readOnly = true)
    public DocumentStatisticsResponse getOverallStatistics() {
        // Count documents by type
        long billetCount = lettreSommationBilletRepository.count();
        long carteCount = lettreSommationCarteRepository.count();
        long rapportCount = rapportMRepository.count();
        long totalDocumentCount = billetCount + carteCount + rapportCount;

        // Create document counts by type map
        Map<String, Long> documentCountsByType = new HashMap<>();
        documentCountsByType.put("LETTRE_BILLET", billetCount);
        documentCountsByType.put("LETTRE_CARTE", carteCount);
        documentCountsByType.put("RAPPORT_M", rapportCount);

        // Count files by document type
        long billetFileCount = pieceJointeRepository.countByTypeDocumentAndDocumentId(TypeDocumentEnum.LETTRE_BILLET, null);
        long carteFileCount = pieceJointeRepository.countByTypeDocumentAndDocumentId(TypeDocumentEnum.LETTRE_CARTE, null);
        long rapportFileCount = pieceJointeRepository.countByTypeDocumentAndDocumentId(TypeDocumentEnum.RAPPORT_M, null);
        long totalFileCount = billetFileCount + carteFileCount + rapportFileCount;

        // Create file counts by type map
        Map<String, Long> fileCountsByDocumentType = new HashMap<>();
        fileCountsByDocumentType.put("LETTRE_BILLET", billetFileCount);
        fileCountsByDocumentType.put("LETTRE_CARTE", carteFileCount);
        fileCountsByDocumentType.put("RAPPORT_M", rapportFileCount);

        // Get storage size
        long totalStorageSize = fileStorageService.getTotalStorageSize();

        // Create storage size by document type map
        Map<String, Long> storageSizeByDocumentType = new HashMap<>();
        storageSizeByDocumentType.put("LETTRE_BILLET", totalStorageSize / 3);
        storageSizeByDocumentType.put("LETTRE_CARTE", totalStorageSize / 3);
        storageSizeByDocumentType.put("RAPPORT_M", totalStorageSize / 3);

        // Format storage sizes
        Map<String, String> storageSizeByDocumentTypeFormatted = new HashMap<>();
        for (Map.Entry<String, Long> entry : storageSizeByDocumentType.entrySet()) {
            storageSizeByDocumentTypeFormatted.put(entry.getKey(), formatFileSize(entry.getValue()));
        }

        // Build response
        return DocumentStatisticsResponse.builder()
                .totalDocumentCount(totalDocumentCount)
                .documentCountsByType(documentCountsByType)
                .totalFileCount(totalFileCount)
                .fileCountsByDocumentType(fileCountsByDocumentType)
                .totalStorageSize(totalStorageSize)
                .totalStorageSizeFormatted(formatFileSize(totalStorageSize))
                .storageSizeByDocumentType(storageSizeByDocumentType)
                .storageSizeByDocumentTypeFormatted(storageSizeByDocumentTypeFormatted)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentStatisticsResponse getStatisticsByDocumentType(TypeDocumentEnum typeDocument) {

        DocumentStatisticsResponse overall = getOverallStatistics();

        // Filter to just the requested document type
        Map<String, Long> documentCountsByType = new HashMap<>();
        documentCountsByType.put(typeDocument.name(), overall.getDocumentCountsByType().get(typeDocument.name()));

        Map<String, Long> fileCountsByDocumentType = new HashMap<>();
        fileCountsByDocumentType.put(typeDocument.name(), overall.getFileCountsByDocumentType().get(typeDocument.name()));

        Map<String, Long> storageSizeByDocumentType = new HashMap<>();
        storageSizeByDocumentType.put(typeDocument.name(), overall.getStorageSizeByDocumentType().get(typeDocument.name()));

        Map<String, String> storageSizeByDocumentTypeFormatted = new HashMap<>();
        storageSizeByDocumentTypeFormatted.put(typeDocument.name(), overall.getStorageSizeByDocumentTypeFormatted().get(typeDocument.name()));

        return DocumentStatisticsResponse.builder()
                .totalDocumentCount(documentCountsByType.get(typeDocument.name()))
                .documentCountsByType(documentCountsByType)
                .totalFileCount(fileCountsByDocumentType.get(typeDocument.name()))
                .fileCountsByDocumentType(fileCountsByDocumentType)
                .totalStorageSize(storageSizeByDocumentType.get(typeDocument.name()))
                .totalStorageSizeFormatted(formatFileSize(storageSizeByDocumentType.get(typeDocument.name())))
                .storageSizeByDocumentType(storageSizeByDocumentType)
                .storageSizeByDocumentTypeFormatted(storageSizeByDocumentTypeFormatted)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentStatisticsResponse getStatisticsByDateRange(LocalDate startDate, LocalDate endDate) {
        return getOverallStatistics();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentStatisticsResponse getStatisticsForDocument(TypeDocumentEnum typeDocument, Long documentId) {
        // Get file count for this document
        long fileCount = pieceJointeRepository.countByTypeDocumentAndDocumentId(typeDocument, documentId);

        // Get storage size for this document
        long storageSize = fileStorageService.getDocumentStorageSize(typeDocument, documentId);

        // Create maps
        Map<String, Long> documentCountsByType = new HashMap<>();
        documentCountsByType.put(typeDocument.name(), 1L);

        Map<String, Long> fileCountsByDocumentType = new HashMap<>();
        fileCountsByDocumentType.put(typeDocument.name(), fileCount);

        Map<String, Long> storageSizeByDocumentType = new HashMap<>();
        storageSizeByDocumentType.put(typeDocument.name(), storageSize);

        Map<String, String> storageSizeByDocumentTypeFormatted = new HashMap<>();
        storageSizeByDocumentTypeFormatted.put(typeDocument.name(), formatFileSize(storageSize));

        return DocumentStatisticsResponse.builder()
                .totalDocumentCount(1L)
                .documentCountsByType(documentCountsByType)
                .totalFileCount(fileCount)
                .fileCountsByDocumentType(fileCountsByDocumentType)
                .totalStorageSize(storageSize)
                .totalStorageSizeFormatted(formatFileSize(storageSize))
                .storageSizeByDocumentType(storageSizeByDocumentType)
                .storageSizeByDocumentTypeFormatted(storageSizeByDocumentTypeFormatted)
                .build();
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}