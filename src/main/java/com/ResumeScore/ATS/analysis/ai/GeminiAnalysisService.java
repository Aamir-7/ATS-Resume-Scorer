package com.ResumeScore.ATS.analysis.ai;

import com.ResumeScore.ATS.analysis.ai.dto.GeminiAnalysisRequest;
import com.ResumeScore.ATS.analysis.ai.dto.GeminiAnalysisResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiAnalysisService {

    private final GeminiClient geminiClient;

    public GeminiAnalysisService(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    public GeminiAnalysisResponse analyzeResume(GeminiAnalysisRequest request) {
        validateRequest(request);

        String responseText = geminiClient.generateContent(buildPrompt(request));
        return parseResponse(responseText);
    }

    private void validateRequest(GeminiAnalysisRequest geminiAnalysisRequest) {
        if (geminiAnalysisRequest == null) {
            throw new IllegalArgumentException("Gemini analysis can not be null ");
        }
        if (geminiAnalysisRequest.getResumeText() == null || geminiAnalysisRequest.getResumeText().isBlank()) {
            throw new IllegalArgumentException("Resume text is required ");
        }
        if (geminiAnalysisRequest.getJobDescription() == null || geminiAnalysisRequest.getJobDescription().isBlank()) {
            throw new IllegalArgumentException("The job description is required ");
        }
    }

    private String buildPrompt(GeminiAnalysisRequest request) {
        return """
                  You are an ATS resume reviewer.
                  
                  Analyze the resume against the job description.
                  Return the response in exactly this format:
                  
                  SUMMARY:
                  <short summary>
                  
                  REWRITE_SUGGESTIONS:
                  - <suggestion 1>
                  - <suggestion 2>
                  
                  KEYWORD_SUGGESTIONS:
                  - <keyword 1>
                  - <keyword 2>
                  
                  IMPROVED_BULLETS:
                  - <bullet 1>
                  - <bullet 2>
                  
                  Match Score: %s
                  Matched Keywords: %s
                  Missing Keywords: %s
                  
                  Resume:
                  %s
                  
                  Job Description:
                  %s
                  """.formatted(
                          request.getMatchScore(),
                        request.getMatchedKeywords(),
                        request.getMissingKeyWords(),
                        request.getResumeText(),
                        request.getJobDescription()
        );
    }

    private GeminiAnalysisResponse parseResponse(String responseText) {
        GeminiAnalysisResponse response = new GeminiAnalysisResponse();
        if (responseText == null || responseText.isBlank()) {
            return response;
        }

        response.setSummary(extractSummary(responseText));
        response.setRewriteSuggestions(extractList(responseText, "REWRITE_SUGGESTIONS:", "KEYWORD_SUGGESTIONS:"));
        response.setKeyWordSuggestions(extractList(responseText, "KEYWORD_SUGGESTIONS:", "IMPROVED_BULLETS:"));
        response.setImprovedBullets(extractList(responseText, "IMPROVED_BULLETS:", null));
        return response;
    }

    private String extractSummary(String text) {
        String summary = extractSection(text, "SUMMARY:", "REWRITE_SUGGESTIONS:");
        return summary == null ? "" : summary.trim();
    }

    private List<String> extractList(String text, String startLabel, String endLabel) {
        String section = extractSection(text, startLabel, endLabel);
        List<String> items = new ArrayList<>();
        if (section == null || section.isBlank()) {
            return items;
        }

        for (String line : section.split("\\R")) {
            String value = line.trim();
            if (value.startsWith("-")) {
                value = value.substring(1).trim();
            }
            if (!value.isBlank()) {
                items.add(value);
            }
        }
        return items;
    }

    private String extractSection(String text, String startLabel, String endLabel) {
        int start = text.indexOf(startLabel);
        if (start < 0) {
            return "";
        }

        start += startLabel.length();
        int end = endLabel == null ? text.length() : text.indexOf(endLabel, start);
        if (end < 0) {
            end = text.length();
        }

        return text.substring(start, end).trim();
    }
}
