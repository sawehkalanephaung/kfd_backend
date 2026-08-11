package com.kfd.api.kfd_backend.media;

import com.kfd.api.kfd_backend.global.exception.StorageException;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Local filesystem storage implementation — active ONLY in the 'local' (dev) profile.
 * 
 * This service handles media uploads by saving them directly to the local disk in 
 * an 'uploads' directory. It provides a simple environment for local development 
 * without requiring AWS credentials or network requests.
 * 
 * In production environments (profile=prod), this bean is ignored and 
 * {@code S3StorageServiceImpl} is injected as the {@code StorageService} instead.
 */
@Profile("local")
@Service
public class LocalStorageServiceImpl implements StorageService {

    private final Path rootLocation = Paths.get("uploads");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown_file");
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = originalFilename.substring(dotIndex);
            }
            
            // Generate a unique file name
            String uniqueFileName = UUID.randomUUID().toString() + extension;
            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFileName)).normalize().toAbsolutePath();

            // Security check to ensure it's saved inside the uploads directory
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside current directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Return the relative URL/path for the client
            return "/uploads/" + uniqueFileName;
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public void delete(String fileUrl) {
        try {
            // Extract the filename from the URL, assuming URL is something like "/uploads/UUID.ext"
            String filename = fileUrl;
            if (fileUrl.startsWith("/uploads/")) {
                filename = fileUrl.substring("/uploads/".length());
            }

            Path file = rootLocation.resolve(filename).normalize().toAbsolutePath();
            
            // Security check
            if (!file.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot delete file outside current directory.");
            }

            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file.", e);
        }
    }
}
