package com.ResumeScore.ATS.analysis;

import com.ResumeScore.ATS.analysis.dto.AnalysisRequest;
import com.ResumeScore.ATS.analysis.dto.AnalysisResponse;
import com.ResumeScore.ATS.job.JobDescription;
import com.ResumeScore.ATS.job.JobDescriptionRepository;
import com.ResumeScore.ATS.resume.Resume;
import com.ResumeScore.ATS.resume.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final KeyWordScoringService keyWordScoringService;

    public AnalysisService(AnalysisRepository analysisRepository, ResumeRepository resumeRepository, JobDescriptionRepository jobDescriptionRepository, KeyWordScoringService keyWordScoringService) {
        this.analysisRepository = analysisRepository;
        this.resumeRepository = resumeRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.keyWordScoringService = keyWordScoringService;
    }

    @Transactional
    public AnalysisResponse createAnalysis(AnalysisRequest request){
        validateRequest(request);

        Resume resume=resumeRepository.findById(request.getResumeId())
                .orElseThrow(()->new IllegalArgumentException("Resume not found "));

        JobDescription jobDescription=jobDescriptionRepository.findById(request.getJobDescriptionId())
                .orElseThrow(()->new IllegalArgumentException("Job description not found "));

        KeyWordScoringService.AnalysisResult result=
                keyWordScoringService.analyze(
                        resume.getExtractedText(),
                        jobDescription.getRawText()
                );

        Analysis analysis=new Analysis();
        analysis.setResume(resume);
        analysis.setJobDescription(jobDescription);
        analysis.setStatus(AnalysisStatus.COMPLETED);
        analysis.setMatchScore(result.getMatchScore());
        analysis.setMatchedKeywords(result.getMatchedKeywords());
        analysis.setMissingKeywords(result.getMissingKeywords());
        analysis.setSuggestions(buildSuggestions(result.getMissingKeywords()));

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

    private void validateRequest(AnalysisRequest request){
        if (request==null){
            throw new IllegalArgumentException("Analysis request can not be empty ");
        }
        if (request.getResumeId()==null){
            throw new IllegalArgumentException("Resume id is required ");
        }
        if (request.getJobDescriptionId()==null){
            throw new IllegalArgumentException("Job description id is required ");
        }
    }
    private String buildSuggestions(List<String>missingKeywords){
        if (missingKeywords==null || missingKeywords.isEmpty()){
            return "Your resume already covers all the main job keywords ";
        }
        return "Consider adding or highlighting these keywords " +
                String.join(", ",missingKeywords);
    }
}
