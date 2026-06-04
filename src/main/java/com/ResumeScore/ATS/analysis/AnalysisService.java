package com.ResumeScore.ATS.analysis;

import com.ResumeScore.ATS.analysis.ai.GeminiAnalysisService;
import com.ResumeScore.ATS.analysis.ai.dto.GeminiAnalysisRequest;
import com.ResumeScore.ATS.analysis.ai.dto.GeminiAnalysisResponse;
import com.ResumeScore.ATS.analysis.dto.AnalysisListResponse;
import com.ResumeScore.ATS.analysis.dto.AnalysisRequest;
import com.ResumeScore.ATS.analysis.dto.AnalysisResponse;
import com.ResumeScore.ATS.job.JobDescription;
import com.ResumeScore.ATS.job.JobDescriptionRepository;
import com.ResumeScore.ATS.resume.Resume;
import com.ResumeScore.ATS.resume.ResumeRepository;
import com.ResumeScore.ATS.user.User;
import com.ResumeScore.ATS.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final AnalysisRepository analysisRepository;
    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final KeyWordScoringService keyWordScoringService;
    private final GeminiAnalysisService geminiAnalysisService;
    private final UserRepository userRepository;

    public AnalysisService(
            AnalysisRepository analysisRepository,
            ResumeRepository resumeRepository,
            JobDescriptionRepository jobDescriptionRepository,
            KeyWordScoringService keyWordScoringService,
            GeminiAnalysisService geminiAnalysisService,
            UserRepository userRepository
    ) {
        this.analysisRepository = analysisRepository;
        this.resumeRepository = resumeRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.keyWordScoringService = keyWordScoringService;
        this.geminiAnalysisService = geminiAnalysisService;
        this.userRepository = userRepository;
    }

    @Transactional
    public AnalysisResponse createAnalysis(AnalysisRequest request, String mode) {
        validateRequest(request);
        User currentUser = getCurrentUser();

        Resume resume = resumeRepository.findByIdAndUserId(request.getResumeId(), currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Resume not found "));

        JobDescription jobDescription = jobDescriptionRepository.findByIdAndUserId(request.getJobDescriptionId(), currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Job description not found "));

        KeyWordScoringService.AnalysisResult result = keyWordScoringService.analyze(
                resume.getExtractedText(),
                jobDescription.getRawText()
        );

        GeminiAnalysisResponse aiResponse = null;
        if (!"manual".equalsIgnoreCase(mode)) {
            aiResponse = generateAiSuggestions(resume, jobDescription, result);
        }

        Analysis analysis = new Analysis();
        analysis.setResume(resume);
        analysis.setJobDescription(jobDescription);
        analysis.setUser(currentUser);
        analysis.setStatus(AnalysisStatus.COMPLETED);
        analysis.setMatchScore(result.getMatchScore());
        analysis.setMatchedKeywords(result.getMatchedKeywords());
        analysis.setMissingKeywords(result.getMissingKeywords());
        analysis.setSuggestions(buildFallbackSuggestion(result.getMissingKeywords(), aiResponse));
        analysis.setSummary(aiResponse!=null ? aiResponse.getSummary() : null);
        analysis.setStrengths(aiResponse!=null ? aiResponse.getStrengths() : List.of());
        analysis.setWeaknesses(aiResponse!=null ? aiResponse.getWeaknesses() : List.of());
        analysis.setAtsRisks(aiResponse!=null ? aiResponse.getAtsRisks() : List.of());
        analysis.setRewriteSuggestions(aiResponse!=null ? aiResponse.getRewriteSuggestions() : List.of());
        analysis.setImprovedBullets(aiResponse!=null ? aiResponse.getImprovedBullets() : List.of());


        Analysis savedAnalysis = analysisRepository.save(analysis);

        return new AnalysisResponse(
                savedAnalysis.getId(),
                resume.getId(),
                jobDescription.getId(),
                savedAnalysis.getMatchScore(),
                savedAnalysis.getMatchedKeywords(),
                savedAnalysis.getMissingKeywords(),
                aiResponse != null ? aiResponse.getSummary() : "",
                aiResponse != null ? aiResponse.getStrengths() : List.of(),
                aiResponse != null ? aiResponse.getWeaknesses() : List.of(),
                aiResponse != null ? aiResponse.getAtsRisks() : List.of(),
                aiResponse != null ? aiResponse.getRewriteSuggestions() : List.of(),
                aiResponse != null ? aiResponse.getImprovedBullets() : List.of(),
                savedAnalysis.getStatus()
        );
    }

    @Transactional(readOnly = true)
    public AnalysisResponse getAnalysisById(Long analysisId) {
        Analysis analysis = analysisRepository.findByIdAndUserId(analysisId, getCurrentUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found"));

        return new AnalysisResponse(
                analysis.getId(),
                analysis.getResume().getId(),
                analysis.getJobDescription().getId(),
                analysis.getMatchScore(),
                analysis.getMatchedKeywords(),
                analysis.getMissingKeywords(),
                analysis.getSummary(),
                analysis.getStrengths(),
                analysis.getWeaknesses(),
                analysis.getAtsRisks(),
                analysis.getRewriteSuggestions(),
                analysis.getImprovedBullets(),
                analysis.getStatus()
        );
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

    private String buildFallbackSuggestion(List<String> missingKeywords, GeminiAnalysisResponse aiResponse) {
        if (aiResponse != null && aiResponse.getSummary() != null && !aiResponse.getSummary().isBlank()) {
            return aiResponse.getSummary();
        }
        if (missingKeywords == null || missingKeywords.isEmpty()) {
            return "Your resume already covers all the main job keywords.";
        }
        return "Consider adding or highlighting these keywords: " + String.join(", ", missingKeywords);
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

    private User getCurrentUser() {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        long userId = Long.parseLong(principal);
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<AnalysisListResponse> getMyAnalyses() {
        User currentUser=getCurrentUser();
        return analysisRepository.findAllByUserId(currentUser.getId())
                .stream()
                .map(analysis->new AnalysisListResponse(
                        analysis.getId(),
                        analysis.getResume().getId(),
                        analysis.getJobDescription().getId(),
                        analysis.getMatchScore(),
                        analysis.getStatus(),
                        analysis.getCreatedAt()
                )).toList();

    }
}
