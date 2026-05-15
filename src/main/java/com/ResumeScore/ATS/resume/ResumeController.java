package com.ResumeScore.ATS.resume;

import com.ResumeScore.ATS.common.ApiResponse;
import com.ResumeScore.ATS.resume.dto.ResumeUploadResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
}
