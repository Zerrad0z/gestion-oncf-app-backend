package com.oncf.gare_app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.file-storage")
@Getter
@Setter
public class FileStorageProperties {

    /**
     * Base directory for file uploads
     */
    private String uploadDir = "./uploads";

    /**
     * Maximum file size in bytes (default: 10MB)
     */
    private long maxFileSize = 10 * 1024 * 1024;

    /**
     * Maximum total storage size per document in bytes (default: 50MB)
     */
    private long maxStorageSizePerDocument = 50 * 1024 * 1024;

    /**
     * Allowed file extensions (comma-separated)
     */
    private String allowedExtensions = ".pdf,.jpg,.jpeg,.png,.doc,.docx,.xls,.xlsx,.txt";

    /**
     * Allowed MIME types (comma-separated)
     */
    private String allowedMimeTypes = "application/pdf,image/jpeg,image/png,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,text/plain";
}