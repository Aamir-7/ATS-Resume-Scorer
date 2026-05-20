package com.ResumeScore.ATS.analysis;

import com.ResumeScore.ATS.analysis.dto.AnalysisRequest;
import com.ResumeScore.ATS.analysis.dto.AnalysisResponse;
import com.ResumeScore.ATS.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analyses")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AnalysisResponse>> createAnalysis(
            @RequestBody AnalysisRequest request,
            @RequestParam(defaultValue = "auto") String mode
    ) {
        AnalysisResponse response = analysisService.createAnalysis(request, mode);
        return ResponseEntity.ok(
                ApiResponse.success("Analysis created successfully", response)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnalysisResponse>> getAnalysisById(
            @PathVariable Long id
    ) {
        AnalysisResponse response = analysisService.getAnalysisById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Analysis fetched successfully", response)
        );
    }
}
