package com.ResumeScore.ATS.resume;

import com.ResumeScore.ATS.common.ApiResponse;
import com.ResumeScore.ATS.resume.dto.ResumeDetailResponse;
import com.ResumeScore.ATS.resume.dto.ResumeListResponse;
import com.ResumeScore.ATS.resume.dto.ResumeUploadResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ResumeUploadResponse>> uploadResume(
            @RequestParam("file") MultipartFile file
    ) {
        ResumeUploadResponse response = resumeService.uploadResume(file);
        return ResponseEntity.ok(ApiResponse.success("Resume uploaded successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResumeListResponse>>> getMyResumes() {
        List<ResumeListResponse> response = resumeService.getMyResumes();
        return ResponseEntity.ok(
                ApiResponse.success("Resumes fetched successfully", response)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResumeDetailResponse>> getResumeById(
            @PathVariable Long id
    ) {
        ResumeDetailResponse response = resumeService.getResumeById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Resume fetched successfully", response)
        );
    }
}
