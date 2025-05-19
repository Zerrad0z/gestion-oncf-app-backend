package com.oncf.gare_app.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidationService {

    /**
     * Validate a file for upload
     * @param file The file to validate
     * @return true if valid, false otherwise
     */
    boolean isValidFile(MultipartFile file);

    /**
     * Get validation error message for a file
     * @param file The file to validate
     * @return Error message, or null if valid
     */
    String getValidationErrorMessage(MultipartFile file);

    /**
     * Check if file extension is allowed
     * @param fileName The file name to check
     * @return true if allowed, false otherwise
     */
    boolean isAllowedExtension(String fileName);

    /**
     * Check if MIME type is allowed
     * @param mimeType The MIME type to check
     * @return true if allowed, false otherwise
     */
    boolean isAllowedMimeType(String mimeType);

    /**
     * Check if file size is within limits
     * @param fileSize The file size in bytes
     * @return true if within limits, false otherwise
     */
    boolean isAllowedSize(long fileSize);
}