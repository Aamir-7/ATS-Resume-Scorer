package com.ResumeScore.ATS.analysis;

import com.ResumeScore.ATS.analysis.dto.AnalysisListResponse;
import com.ResumeScore.ATS.analysis.dto.AnalysisRequest;
import com.ResumeScore.ATS.analysis.dto.AnalysisResponse;
import com.ResumeScore.ATS.common.ApiResponse;
import com.ResumeScore.ATS.common.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AnalysisListResponse>>> getMyAnalyses(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<AnalysisListResponse> response = analysisService.getMyAnalyses(pageable);
        return ResponseEntity.ok(
                ApiResponse.success("Analyses fetched successfully", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAnalysis(
            @PathVariable Long id
    ) {
        analysisService.deleteMyAnalysis(id);
        return ResponseEntity.ok(
                ApiResponse.success("Analysis deleted successfully", null)
        );
    }
}
