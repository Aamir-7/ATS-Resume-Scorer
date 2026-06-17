package com.ResumeScore.ATS.report;

import com.ResumeScore.ATS.analysis.Analysis;
import com.ResumeScore.ATS.analysis.AnalysisRepository;
import com.ResumeScore.ATS.common.PageResponse;
import com.ResumeScore.ATS.report.dto.ReportListResponse;
import com.ResumeScore.ATS.user.User;
import com.ResumeScore.ATS.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final AnalysisRepository analysisRepository;
    private final UserRepository userRepository;
    private final String reportDir;

    public ReportService(
            ReportRepository reportRepository,
            AnalysisRepository analysisRepository,
            UserRepository userRepository,
            @Value("${app.storage.report-dir:uploads/reports}") String reportDir
    ) {
        this.reportRepository = reportRepository;
        this.analysisRepository = analysisRepository;
        this.userRepository = userRepository;
        this.reportDir = reportDir;
    }

    @Transactional
    public Report createReport(Long analysisId) {
        User currentUser = getCurrentUser();
        Analysis analysis = analysisRepository.findByIdAndUserId(analysisId, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found"));

        String fileName = "analysis-report-" + analysisId + ".txt";
        Path directory = Path.of(reportDir);
        Path filePath = directory.resolve(fileName);

        writeReportFile(analysis, directory, filePath);

        Report report = reportRepository.findByAnalysisIdAndUserId(analysisId, currentUser.getId())
                .orElseGet(Report::new);
        report.setAnalysis(analysis);
        report.setUser(currentUser);
        report.setFileName(fileName);
        report.setFilePath(filePath.toString());

        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public Resource downloadReport(Long reportId) {
        Report report = reportRepository.findByIdAndUserId(reportId, getCurrentUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        try {
            Path path = Path.of(report.getFilePath());
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException("Report file not found");
            }

            return resource;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load report file");
        }
    }

    private void writeReportFile(Analysis analysis, Path directory, Path
            filePath) {
        try {
            Files.createDirectories(directory);
            Files.writeString(filePath, buildReportContent(analysis));
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to create report file");
        }
    }

    @Transactional(readOnly = true)
    public Report getReportById(Long reportId) {
        return reportRepository.findByIdAndUserId(reportId, getCurrentUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReportListResponse> getMyReports(Pageable pageable) {
        User currentUser = getCurrentUser();

        Page<Report> page = reportRepository.findAllByUserId(currentUser.getId(), pageable);
        List<ReportListResponse> content = page.getContent()
                .stream()
                .map(report -> new ReportListResponse(
                        report.getId(),
                        report.getAnalysis().getId(),
                        report.getFileName(),
                        report.getFilePath(),
                        report.getCreatedAt()
                ))
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    private String buildReportContent(Analysis analysis) {
        return """
                ATS Resume Analysis Report
                ==========================
                
                Generated At: %s
                Analysis Id: %d
                Resume Id: %d
                Job Description Id: %d
                Status: %s
                Match Score: %.2f
                
                Matched Keywords:
                %s
                
                Missing Keywords:
                %s
                
                Summary:
                %s
                
                Strengths:
                %s
                
                Weaknesses:
                %s
                
                ATS Risks:
                %s
                
                Rewrite Suggestions:
                %s
                
                Improved Bullets:
                %s
                """.formatted(
                LocalDateTime.now(),
                analysis.getId(),
                analysis.getResume().getId(),
                analysis.getJobDescription().getId(),
                analysis.getStatus(),
                analysis.getMatchScore(),
                joinList(analysis.getMatchedKeywords()),
                joinList(analysis.getMissingKeywords()),
                safeText(analysis.getSummary()),
                joinList(analysis.getStrengths()),
                joinList(analysis.getWeaknesses()),
                joinList(analysis.getAtsRisks()),
                joinList(analysis.getRewriteSuggestions()),
                joinList(analysis.getImprovedBullets())
        );
    }

    private String joinList(java.util.List<String> items) {
        return (items == null || items.isEmpty()) ? "None" : String.join(", ",
                items);
    }

    private String safeText(String value) {
        return (value == null || value.isBlank()) ? "Not available" : value;
    }

    private User getCurrentUser() {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        long userId = Long.parseLong(principal);
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void deleteMyReport(Long reportId) {
        User currentUser=getCurrentUser();
        Report report=reportRepository.findByIdAndUserId(reportId, currentUser.getId())
                .orElseThrow(()->new IllegalArgumentException("report not found "));
        deleteIfFileExists(report.getFilePath());

        reportRepository.delete(report);

    }

    private void deleteIfFileExists(String filePath){
        if (filePath ==null || filePath.isBlank()){
            return;
        }
        try {
            Files.deleteIfExists(Path.of(filePath));
        } catch (IOException e) {
            throw new IllegalStateException("file can not be deleted ");
        }
    }
}
        
