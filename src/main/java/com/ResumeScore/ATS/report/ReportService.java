package com.ResumeScore.ATS.report;

import com.ResumeScore.ATS.analysis.Analysis;
import com.ResumeScore.ATS.analysis.AnalysisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final AnalysisRepository analysisRepository;
    private final String reportDir;

    public ReportService(
            ReportRepository reportRepository,
            AnalysisRepository analysisRepository,
            @Value("${app.storage.report-dir:uploads/reports}") String reportDir
    ) {
        this.reportRepository = reportRepository;
        this.analysisRepository = analysisRepository;
        this.reportDir = reportDir;
    }

    @Transactional
    public Report createReport(Long analysisId) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found"));

        String fileName = "analysis-report-" + analysisId + ".txt";
        Path directory = Path.of(reportDir);
        Path filePath = directory.resolve(fileName);

        writeReportFile(analysis, directory, filePath);

        Report report = reportRepository.findByAnalysisId(analysisId)
                .orElseGet(Report::new);
        report.setAnalysis(analysis);
        report.setFileName(fileName);
        report.setFilePath(filePath.toString());

        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public Resource downloadReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
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
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
    }

    private String buildReportContent(Analysis analysis) {
        return """
                  ATS Resume Analysis Report                                      \s
                  ==========================                                      \s
                                                                                  \s
                  Generated At: %s
                  Analysis Id: %d                                                 \s
                  Resume Id: %d                                                   \s
                  Job Description Id: %d                                          \s
                  Status: %s
                  Match Score: %.2f
                                                                                  \s
                  Matched Keywords:                                               \s
                  %s                                                              \s
                                                                                  \s
                  Missing Keywords:                                               \s
                  %s
                                                                                  \s
                  Suggestions:                                                    \s
                  %s                                                              \s
                 \s""".formatted(
                LocalDateTime.now(),
                analysis.getId(),
                analysis.getResume().getId(),
                analysis.getJobDescription().getId(),
                analysis.getStatus(),
                analysis.getMatchScore(),
                String.join(", ", analysis.getMatchedKeywords()),
                String.join(", ", analysis.getMissingKeywords()),
                analysis.getSuggestions()
        );
    }
}
        
