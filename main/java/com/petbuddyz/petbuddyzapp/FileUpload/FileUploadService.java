package com.petbuddyz.petbuddyzapp.FileUpload;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileUploadService {
    public String saveFile(MultipartFile file) {
        try {
            // Get the original filename
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new RuntimeException("Original filename is null");
            }

            // Get the directory where FileUploadService.java resides
            String currentDir = System.getProperty("user.dir");

            // Construct the path for storing the file
            String uploadDir = currentDir + File.separator + "UploadedFiles";

            // Create the directory if it doesn't exist
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Save the file to the upload directory
            Path filePath = Paths.get(uploadDir, originalFilename);
            file.transferTo(filePath);

            return originalFilename;
        } catch (Exception e) {
            throw new RuntimeException("Could not save file. Error: " + e.getMessage());
        }
    }
}