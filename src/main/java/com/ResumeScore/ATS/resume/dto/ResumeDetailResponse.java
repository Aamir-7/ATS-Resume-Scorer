package com.ResumeScore.ATS.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResumeDetailResponse {

    private Long id;
    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private String storagePath;
    private String extractedText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
