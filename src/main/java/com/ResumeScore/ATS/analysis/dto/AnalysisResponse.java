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
    private double matchScore;
    private List<String>matchedKeyWords;
    private List<String>missingKeywords;
    private String Suggestions;
    private AnalysisStatus status;
}
