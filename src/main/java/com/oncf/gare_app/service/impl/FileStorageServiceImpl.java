package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.entity.PieceJointe;
import com.oncf.gare_app.enums.TypeDocumentEnum;
import com.oncf.gare_app.exception.FileStorageException;
import com.oncf.gare_app.repository.PieceJointeRepository;
import com.oncf.gare_app.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private  Path fileStorageLocation;
    private  PieceJointeRepository pieceJointeRepository;

    @Autowired
    public FileStorageServiceImpl(@Value("${file.upload-dir}") String uploadDir, PieceJointeRepository pieceJointeRepository) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.pieceJointeRepository = pieceJointeRepository;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Impossible de créer le répertoire où les fichiers seront stockés.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String prefix) throws IOException {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Check if the file's name contains invalid characters
        if (originalFileName.contains("..")) {
            throw new FileStorageException("Le nom du fichier contient une séquence de chemin invalide " + originalFileName);
        }

        // Generate unique file name
        String fileExtension = "";
        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = prefix + "-" + UUID.randomUUID().toString() + fileExtension;

        // Copy file to the target location
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    @Override
    public Path getFilePath(String fileName) {
        return this.fileStorageLocation.resolve(fileName);
    }

    @Override
    public void deleteFile(String fileName) throws IOException {
        Path filePath = this.fileStorageLocation.resolve(fileName);
        Files.deleteIfExists(filePath);
    }

    @Override
    public List<String> storeFiles(List<MultipartFile> files, String prefix) throws IOException {
        List<String> fileNames = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = storeFile(file, prefix);
                    fileNames.add(fileName);
                }
            }
        }

        return fileNames;
    }

    @Override
    public PieceJointe createPieceJointe(MultipartFile file, TypeDocumentEnum typeDocument, Long documentId) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        String filePrefix = typeDocument.name().toLowerCase().replace('_', '-') + "-" + documentId;
        String fileName = storeFile(file, filePrefix);

        return PieceJointe.builder()
                .typeDocument(typeDocument)
                .documentId(documentId)
                .nomFichier(file.getOriginalFilename())
                .cheminFichier(fileName)
                .typeMime(file.getContentType())
                .taille(file.getSize())
                .build();
    }

    @Override
    public List<PieceJointe> createPiecesJointes(List<MultipartFile> files, TypeDocumentEnum typeDocument, Long documentId) throws IOException {
        List<PieceJointe> piecesJointes = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                PieceJointe pieceJointe = createPieceJointe(file, typeDocument, documentId);
                if (pieceJointe != null) {
                    piecesJointes.add(pieceJointe);
                }
            }
        }

        return piecesJointes;
    }

    @Override
    public boolean fileExists(String fileName) {
        Path filePath = this.fileStorageLocation.resolve(fileName);
        return Files.exists(filePath);
    }

    @Override
    public long getTotalStorageSize() {
        try {
            return Files.walk(this.fileStorageLocation)
                    .filter(Files::isRegularFile)
                    .mapToLong(path -> {
                        try {
                            return Files.size(path);
                        } catch (IOException e) {
                            return 0L;
                        }
                    })
                    .sum();
        } catch (IOException e) {
            throw new FileStorageException("Erreur lors du calcul de la taille totale du stockage", e);
        }
    }

    @Override
    public long getDocumentStorageSize(TypeDocumentEnum typeDocument, Long documentId) {
        // Use the repository to get the total size of all pieces jointes for this document
        Long totalSize = pieceJointeRepository.getTotalSizeByTypeDocumentAndDocumentId(typeDocument, documentId);
        return totalSize != null ? totalSize : 0L;
    }
}