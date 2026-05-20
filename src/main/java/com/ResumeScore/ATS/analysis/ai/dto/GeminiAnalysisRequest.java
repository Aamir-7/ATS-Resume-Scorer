package com.ResumeScore.ATS.analysis.ai.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GeminiAnalysisRequest {
    private String resumeText;
    private String jobDescription;
    private List<String>matchedKeywords;
    private List<String>missingKeyWords;
    private double matchScore;
}
