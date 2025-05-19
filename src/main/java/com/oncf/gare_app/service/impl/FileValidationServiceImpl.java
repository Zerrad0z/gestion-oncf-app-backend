package com.oncf.gare_app.service.impl;

import com.oncf.gare_app.config.FileStorageProperties;
import com.oncf.gare_app.service.FileValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileValidationServiceImpl implements FileValidationService {

    private final FileStorageProperties fileStorageProperties;

    @Override
    public boolean isValidFile(MultipartFile file) {
        if (file.isEmpty()) {
            return false;
        }

        String fileName = file.getOriginalFilename();
        String mimeType = file.getContentType();
        long fileSize = file.getSize();

        return isAllowedExtension(fileName) && isAllowedMimeType(mimeType) && isAllowedSize(fileSize);
    }

    @Override
    public String getValidationErrorMessage(MultipartFile file) {
        if (file.isEmpty()) {
            return "Le fichier est vide";
        }

        String fileName = file.getOriginalFilename();
        String mimeType = file.getContentType();
        long fileSize = file.getSize();

        if (!isAllowedExtension(fileName)) {
            return "L'extension du fichier n'est pas autorisée. Extensions autorisées: " + fileStorageProperties.getAllowedExtensions();
        }

        if (!isAllowedMimeType(mimeType)) {
            return "Le type MIME du fichier n'est pas autorisé. Types MIME autorisés: " + fileStorageProperties.getAllowedMimeTypes();
        }

        if (!isAllowedSize(fileSize)) {
            return "La taille du fichier dépasse la limite autorisée de " + (fileStorageProperties.getMaxFileSize() / (1024 * 1024)) + " Mo";
        }

        return null;
    }

    @Override
    public boolean isAllowedExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        List<String> allowedExtensions = Arrays.asList(fileStorageProperties.getAllowedExtensions().split(","));

        return allowedExtensions.contains(extension);
    }

    @Override
    public boolean isAllowedMimeType(String mimeType) {
        if (mimeType == null || mimeType.isEmpty()) {
            return false;
        }

        List<String> allowedMimeTypes = Arrays.asList(fileStorageProperties.getAllowedMimeTypes().split(","));

        return allowedMimeTypes.contains(mimeType);
    }

    @Override
    public boolean isAllowedSize(long fileSize) {
        return fileSize > 0 && fileSize <= fileStorageProperties.getMaxFileSize();
    }
}