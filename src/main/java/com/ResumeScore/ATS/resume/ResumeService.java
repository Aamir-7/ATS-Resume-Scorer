package com.ResumeScore.ATS.resume;

import com.ResumeScore.ATS.resume.dto.ResumeUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ResumeService {

    private final ResumeParserService resumeParserService;
    private final ResumeRepository resumeRepository;
    private final String resumeDir;

    public ResumeService(
            ResumeParserService resumeParserService,
            ResumeRepository resumeRepository,
            @Value("${app.storage.resume-dir:uploads/resumes}") String resumeDir
    ) {
        this.resumeParserService = resumeParserService;
        this.resumeRepository = resumeRepository;
        this.resumeDir = resumeDir;
    }

    public ResumeUploadResponse uploadResume(MultipartFile file) {
        validateResume(file);

        String originalFileName = file.getOriginalFilename() == null
                ? "resume.pdf"
                : file.getOriginalFilename();
        String extension = getExtension(originalFileName);
        String storedFileName = UUID.randomUUID() + extension;

        Path directory = Path.of(resumeDir);
        Path filePath = directory.resolve(storedFileName);

        saveFile(file, directory, filePath);
        String extractedText = resumeParserService.extractText(filePath);

        Resume resume = new Resume();
        resume.setOriginalFileName(originalFileName);
        resume.setStoredFileName(storedFileName);
        resume.setContentType(file.getContentType() == null ? "application/pdf" : file.getContentType());
        resume.setStoragePath(filePath.toString());
        resume.setExtractedText(extractedText);

        Resume savedResume = resumeRepository.save(resume);

        return new ResumeUploadResponse(
                savedResume.getId(),
                originalFileName,
                storedFileName,
                savedResume.getContentType(),
                filePath.toString(),
                "Resume uploaded successfully"
        );
    }

    private void validateResume(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.equalsIgnoreCase("application/pdf")) {
            throw new IllegalArgumentException("Invalid file type. Please upload a PDF");
        }
    }

    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex >= 0 ? fileName.substring(lastDotIndex) : "";
    }

    private void saveFile(MultipartFile file, Path directory, Path filePath) {
        try {
            Files.createDirectories(directory);
            file.transferTo(filePath);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to save resume file");
        }
    }
}
