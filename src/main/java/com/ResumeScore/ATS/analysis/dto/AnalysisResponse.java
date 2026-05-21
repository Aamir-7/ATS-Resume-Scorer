package com.ResumeScore.ATS.analysis.dto;

import com.ResumeScore.ATS.analysis.AnalysisStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AnalysisResponse {

    private long analysisId;
    private long resumeId;
    private long jobDescriptionId;
    private double matchScore;
    private List<String> matchedKeywords;
    private List<String> missingKeywords;
    private String summary;
    private List<String>strengths;
    private List<String>weaknesses;
    private List<String>atsRisks;
    private List<String> rewriteSuggestions;
    private List<String>improvedBullets;
    private AnalysisStatus status;
}
