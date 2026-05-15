package com.ResumeScore.ATS.resume.dto;

public class ResumeUploadResponse {

    private Long resumeId;
    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private String storagePath;
    private String message;

    public ResumeUploadResponse() {
    }

    public ResumeUploadResponse(
            Long resumeId,
            String originalFileName,
            String storedFileName,
            String contentType,
            String storagePath,
            String message
    ) {
        this.resumeId = resumeId;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.contentType = contentType;
        this.storagePath = storagePath;
        this.message = message;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
