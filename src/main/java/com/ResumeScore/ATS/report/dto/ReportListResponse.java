package com.ResumeScore.ATS.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportListResponse {

    private long reportId;
    private long analysisId;
    private String fileName;
    private String filePath;
    private LocalDateTime createdAt;
}
