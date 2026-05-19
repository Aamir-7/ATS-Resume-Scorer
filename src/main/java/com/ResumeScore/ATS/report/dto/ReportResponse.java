package com.ResumeScore.ATS.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {

    private long reportId;
    private long analysisId;
    private String fileName;
    private String filePath;
    private String message;
}
