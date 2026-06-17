package com.ResumeScore.ATS.analysis.dashboard;

import com.ResumeScore.ATS.analysis.AnalysisRepository;
import com.ResumeScore.ATS.job.JobDescriptionRepository;
import com.ResumeScore.ATS.report.ReportRepository;
import com.ResumeScore.ATS.resume.ResumeRepository;
import com.ResumeScore.ATS.user.User;
import com.ResumeScore.ATS.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class DashBoardService {

    private final AnalysisRepository analysisRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final ReportRepository reportRepository;

    public DashBoardService(AnalysisRepository analysisRepository, UserRepository userRepository, ResumeRepository resumeRepository, JobDescriptionRepository jobDescriptionRepository, ReportRepository reportRepository) {
        this.analysisRepository = analysisRepository;
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.reportRepository = reportRepository;
    }

    public DashBoardSummaryResponse getSummary() {
        User currentUser=getCurrentUser();

        Long totalResume=resumeRepository.countByUserId(currentUser.getId());
        Long totalJobDesc=jobDescriptionRepository.countByUserId(currentUser.getId());
        Long totalAnalysis=analysisRepository.countByUserId(currentUser.getId());
        Long totalReports=reportRepository.countByUserId(currentUser.getId());
        Double avgScore=analysisRepository.findAverageMatchScoreByUserId(currentUser.getId());
        Double bestScore=analysisRepository.findBestScoreByUserId(currentUser.getId());
        Long latestAnalysisId =analysisRepository.findTopByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .map(analysis -> analysis.getId())
                .orElse(null);

        DashBoardSummaryResponse response=new DashBoardSummaryResponse();
        response.setTotalResumes(totalResume);
        response.setTotalJobDescriptions(totalJobDesc);
        response.setTotalAnalyses(totalAnalysis);
        response.setTotalReports(totalReports);
        response.setAverageMatchScore(avgScore == null ? 0.0 : avgScore);
        response.setBestMatchScore(bestScore == null ? 0.0 : bestScore);
        response.setLatestAnalysisId(latestAnalysisId);

        return response;


    }

    private User getCurrentUser(){
        String principal= SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId=Long.parseLong(principal);
        return userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("user not found"));
    }


}
