package com.GreenThumb.GT.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.uploadDir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        try {
            Path fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(fileStorageLocation);

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);

            return targetLocation.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename() + ". Please try again!", ex);
        }
    }

    public Path loadFilePath(String fileName) {
        Path fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        return fileStorageLocation.resolve(fileName).normalize();
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = loadFilePath(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Error: Malformed URL", ex);
        }
    }
}