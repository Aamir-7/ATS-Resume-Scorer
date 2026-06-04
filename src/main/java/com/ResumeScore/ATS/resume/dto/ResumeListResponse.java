package com.ResumeScore.ATS.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResumeListResponse {

    public Long id;

    public String originalFileName;

    public String storedFileName;

    public String contentType;

    public LocalDateTime createdAt;
}
