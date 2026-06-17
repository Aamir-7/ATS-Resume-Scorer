package com.ResumeScore.ATS.resume;

import com.ResumeScore.ATS.common.ApiResponse;
import com.ResumeScore.ATS.common.PageResponse;
import com.ResumeScore.ATS.resume.dto.ResumeDetailResponse;
import com.ResumeScore.ATS.resume.dto.ResumeListResponse;
import com.ResumeScore.ATS.resume.dto.ResumeUploadResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<ApiResponse<PageResponse<ResumeListResponse>>> getMyResumes(
            @PageableDefault(size = 10,sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<ResumeListResponse> response = resumeService.getMyResumes(pageable);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>>deleteResume(
            @PathVariable Long id
    ){
        resumeService.deleteMyResume(id);
        return ResponseEntity.ok(
                ApiResponse.success("Resume deleted successfully ",null)
        );
    }
}
