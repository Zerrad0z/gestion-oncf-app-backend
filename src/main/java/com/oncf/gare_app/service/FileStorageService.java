package com.oncf.gare_app.service;

import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileStorageService {

    // Basic file operations
    String storeFile(MultipartFile file, String prefix) throws IOException;

    Path getFilePath(String fileName);

    void deleteFile(String fileName) throws IOException;

    List<String> storeFiles(List<MultipartFile> files, String prefix) throws IOException;

    // Document attachment operations
    PieceJointe createPieceJointe(MultipartFile file, TypeDocumentEnum typeDocument, Long documentId) throws IOException;

    List<PieceJointe> createPiecesJointes(List<MultipartFile> files, TypeDocumentEnum typeDocument, Long documentId) throws IOException;

    // Additional utility methods
    boolean fileExists(String fileName);

    long getTotalStorageSize();

    long getDocumentStorageSize(TypeDocumentEnum typeDocument, Long documentId);
}