package com.ResumeScore.ATS.analysis.ai.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GeminiAnalysisResponse {
    private String summary;
    private List<String>rewriteSuggestions;
    private List<String>keyWordSuggestions;
    private List<String>improvedBullets;
}
