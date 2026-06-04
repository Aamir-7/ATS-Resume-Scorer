package com.ResumeScore.ATS.report;

import com.ResumeScore.ATS.common.ApiResponse;
import com.ResumeScore.ATS.report.dto.ReportListResponse;
import com.ResumeScore.ATS.report.dto.ReportResponse;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/{analysisId}")
    public ResponseEntity<ApiResponse<ReportResponse>>createReport(
        @PathVariable Long analysisId
    ){
        Report report= reportService.createReport(analysisId);
        ReportResponse response=new ReportResponse(
                report.getId(),
                report.getAnalysis().getId(),
                report.getFileName(),
                report.getFilePath(),
                "Report created successfully "
        );
        return ResponseEntity.ok(
                ApiResponse.success("Report created successfully",response)
        );
    }

    @GetMapping("/{reportId}/download")
    public ResponseEntity<Resource>downloadReport(
            @PathVariable Long reportId
    ){
        Resource resource=reportService.downloadReport(reportId);
        Report report=reportService.getReportById(reportId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""+report.getFileName()+"\"")
                .body(resource);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReportListResponse>>> getMyReports() {
        List<ReportListResponse> response = reportService.getMyReports();
        return ResponseEntity.ok(
                ApiResponse.success("Reports fetched successfully", response)
        );
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ReportResponse>> getReportById(
            @PathVariable Long reportId
    ) {
        Report report = reportService.getReportById(reportId);
        ReportResponse response = new ReportResponse(
                report.getId(),
                report.getAnalysis().getId(),
                report.getFileName(),
                report.getFilePath(),
                "Report fetched successfully"
        );
        return ResponseEntity.ok(
                ApiResponse.success("Report fetched successfully", response)
        );
    }
}
