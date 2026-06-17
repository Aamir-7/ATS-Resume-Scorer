package com.ResumeScore.ATS.resume;

import com.ResumeScore.ATS.common.PageResponse;
import com.ResumeScore.ATS.resume.dto.ResumeDetailResponse;
import com.ResumeScore.ATS.resume.dto.ResumeListResponse;
import com.ResumeScore.ATS.resume.dto.ResumeUploadResponse;
import com.ResumeScore.ATS.user.User;
import com.ResumeScore.ATS.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class ResumeService {

    private final ResumeParserService resumeParserService;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final String resumeDir;

    public ResumeService(
            ResumeParserService resumeParserService,
            ResumeRepository resumeRepository,
            UserRepository userRepository,
            @Value("${app.storage.resume-dir:uploads/resumes}") String resumeDir
    ) {
        this.resumeParserService = resumeParserService;
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
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
        User currentUser = getCurrentUser();

        Resume resume = new Resume();
        resume.setOriginalFileName(originalFileName);
        resume.setStoredFileName(storedFileName);
        resume.setContentType(file.getContentType() == null ? "application/pdf" : file.getContentType());
        resume.setStoragePath(filePath.toString());
        resume.setExtractedText(extractedText);
        resume.setUser(currentUser);

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

    private User getCurrentUser() {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        long userId = Long.parseLong(principal);
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public PageResponse<ResumeListResponse> getMyResumes(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Resume>page=resumeRepository.findAllByUserId(currentUser.getId(),pageable);

        List<ResumeListResponse>content=page.getContent()
                .stream()
                .map(resume -> new ResumeListResponse(
                        resume.getId(),
                        resume.getOriginalFileName(),
                        resume.getStoredFileName(),
                        resume.getContentType(),
                        resume.getCreatedAt()
                )).toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public ResumeDetailResponse getResumeById(Long id) {
        User currentUser = getCurrentUser();
        Resume resume = resumeRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        return new ResumeDetailResponse(
                resume.getId(),
                resume.getOriginalFileName(),
                resume.getStoredFileName(),
                resume.getContentType(),
                resume.getStoragePath(),
                resume.getExtractedText(),
                resume.getCreatedAt(),
                resume.getUpdatedAt()
        );
    }

    public void deleteMyResume(Long id) {
        User currentUser=getCurrentUser();
        Resume resume=resumeRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(()->new IllegalArgumentException("resume not found "));

        deleteFileIfExists(resume.getStoragePath());

        resumeRepository.delete(resume);
    }

    private void deleteFileIfExists(String filePath) {
        try {
            Files.deleteIfExists(Path.of(filePath));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to delete resume file");
        }
    }

}
