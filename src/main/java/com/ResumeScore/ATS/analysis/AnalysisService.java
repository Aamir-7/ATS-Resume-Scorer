package com.ResumeScore.ATS.analysis;

import com.ResumeScore.ATS.analysis.ai.GeminiAnalysisService;
import com.ResumeScore.ATS.analysis.ai.dto.GeminiAnalysisRequest;
import com.ResumeScore.ATS.analysis.ai.dto.GeminiAnalysisResponse;
import com.ResumeScore.ATS.analysis.dto.AnalysisRequest;
import com.ResumeScore.ATS.analysis.dto.AnalysisResponse;
import com.ResumeScore.ATS.job.JobDescription;
import com.ResumeScore.ATS.job.JobDescriptionRepository;
import com.ResumeScore.ATS.resume.Resume;
import com.ResumeScore.ATS.resume.ResumeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final AnalysisRepository analysisRepository;
    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final KeyWordScoringService keyWordScoringService;
    private final GeminiAnalysisService geminiAnalysisService;

    public AnalysisService(
            AnalysisRepository analysisRepository,
            ResumeRepository resumeRepository,
            JobDescriptionRepository jobDescriptionRepository,
            KeyWordScoringService keyWordScoringService,
            GeminiAnalysisService geminiAnalysisService
    ) {
        this.analysisRepository = analysisRepository;
        this.resumeRepository = resumeRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.keyWordScoringService = keyWordScoringService;
        this.geminiAnalysisService = geminiAnalysisService;
    }

    @Transactional
    public AnalysisResponse createAnalysis(AnalysisRequest request, String mode) {
        validateRequest(request);

        Resume resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new IllegalArgumentException("Resume not found "));

        JobDescription jobDescription = jobDescriptionRepository.findById(request.getJobDescriptionId())
                .orElseThrow(() -> new IllegalArgumentException("Job description not found "));

        KeyWordScoringService.AnalysisResult result =
                keyWordScoringService.analyze(
                        resume.getExtractedText(),
                        jobDescription.getRawText()
                );

        String normalizedMode = normalizeMode(mode);
        String suggestions = resolveSuggestions(normalizedMode, resume, jobDescription, result);

        Analysis analysis = new Analysis();
        analysis.setResume(resume);
        analysis.setJobDescription(jobDescription);
        analysis.setStatus(AnalysisStatus.COMPLETED);
        analysis.setMatchScore(result.getMatchScore());
        analysis.setMatchedKeywords(result.getMatchedKeywords());
        analysis.setMissingKeywords(result.getMissingKeywords());
        analysis.setSuggestions(suggestions);

        Analysis savedAnalysis = analysisRepository.save(analysis);

        return new AnalysisResponse(
                savedAnalysis.getId(),
                resume.getId(),
                jobDescription.getId(),
                savedAnalysis.getMatchScore(),
                savedAnalysis.getMatchedKeywords(),
                savedAnalysis.getMissingKeywords(),
                savedAnalysis.getSuggestions(),
                savedAnalysis.getStatus()
        );
    }

    @Transactional(readOnly = true)
    public AnalysisResponse getAnalysisById(Long analysisId) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found"));

        return new AnalysisResponse(
                analysis.getId(),
                analysis.getResume().getId(),
                analysis.getJobDescription().getId(),
                analysis.getMatchScore(),
                analysis.getMatchedKeywords(),
                analysis.getMissingKeywords(),
                analysis.getSuggestions(),
                analysis.getStatus()
        );
    }

    private String resolveSuggestions(
            String mode,
            Resume resume,
            JobDescription jobDescription,
            KeyWordScoringService.AnalysisResult result
    ) {
        if ("manual".equals(mode)) {
            return buildManualSuggestions(result.getMissingKeywords());
        }

        GeminiAnalysisResponse aiResponse = generateAiSuggestions(resume, jobDescription, result);
        String aiSuggestions = buildAiSuggestions(aiResponse);

        if ("ai".equals(mode)) {
            return aiSuggestions == null || aiSuggestions.isBlank()
                    ? "AI suggestions are currently unavailable."
                    : aiSuggestions;
        }

        if (aiSuggestions == null || aiSuggestions.isBlank()) {
            return buildManualSuggestions(result.getMissingKeywords());
        }

        return aiSuggestions;
    }

    private GeminiAnalysisResponse generateAiSuggestions(
            Resume resume,
            JobDescription jobDescription,
            KeyWordScoringService.AnalysisResult result
    ) {
        try {
            GeminiAnalysisRequest aiRequest = new GeminiAnalysisRequest();
            aiRequest.setResumeText(resume.getExtractedText());
            aiRequest.setJobDescription(jobDescription.getRawText());
            aiRequest.setMatchedKeywords(result.getMatchedKeywords());
            aiRequest.setMissingKeyWords(result.getMissingKeywords());
            aiRequest.setMatchScore(result.getMatchScore());
            return geminiAnalysisService.analyzeResume(aiRequest);
        } catch (Exception ex) {
            log.warn("Gemini analysis failed", ex);
            return null;
        }
    }

    private String normalizeMode(String mode) {
        if (mode == null || mode.isBlank()) {
            return "auto";
        }

        String normalizedMode = mode.trim().toLowerCase();
        if (!normalizedMode.equals("auto") && !normalizedMode.equals("ai") && !normalizedMode.equals("manual")) {
            throw new IllegalArgumentException("Mode must be one of: auto, ai, manual");
        }

        return normalizedMode;
    }

    private void validateRequest(AnalysisRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Analysis request can not be empty ");
        }
        if (request.getResumeId() == null) {
            throw new IllegalArgumentException("Resume id is required ");
        }
        if (request.getJobDescriptionId() == null) {
            throw new IllegalArgumentException("Job description id is required ");
        }
    }

    private String buildAiSuggestions(GeminiAnalysisResponse aiResponse) {
        StringJoiner joiner = new StringJoiner("\n\n");

        if (aiResponse == null) {
            return "";
        }

        if (aiResponse.getSummary() != null && !aiResponse.getSummary().isBlank()) {
            joiner.add("AI Summary:\n" + aiResponse.getSummary());
        }

        appendListSection(joiner, "Rewrite Suggestions", aiResponse.getRewriteSuggestions());
        appendListSection(joiner, "Keyword Suggestions", aiResponse.getKeyWordSuggestions());
        appendListSection(joiner, "Improved Bullets", aiResponse.getImprovedBullets());

        return joiner.toString();
    }

    private String buildManualSuggestions(List<String> missingKeywords) {
        if (missingKeywords == null || missingKeywords.isEmpty()) {
            return "Your resume already covers all the main job keywords.";
        }
        return "Consider adding or highlighting these keywords: " + String.join(", ", missingKeywords);
    }

    private void appendListSection(StringJoiner joiner, String title, List<String> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        List<String> lines = new ArrayList<>();
        for (String item : items) {
            if (item != null && !item.isBlank()) {
                lines.add("- " + item);
            }
        }

        if (!lines.isEmpty()) {
            joiner.add(title + ":\n" + String.join("\n", lines));
        }
    }
}
