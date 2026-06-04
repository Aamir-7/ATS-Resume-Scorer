package com.ResumeScore.ATS.analysis.dto;

import com.ResumeScore.ATS.analysis.AnalysisStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisListResponse {

    private Long analysisId;
    private Long resumeId;
    private Long descriptionId;
    private Double matchScore;
    private AnalysisStatus status;
    private LocalDateTime createdAt;
}
