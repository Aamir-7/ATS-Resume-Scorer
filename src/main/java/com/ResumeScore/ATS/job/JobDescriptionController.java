package com.ResumeScore.ATS.job;

import com.ResumeScore.ATS.common.ApiResponse;
import com.ResumeScore.ATS.job.dto.JobDescriptionListResponse;
import com.ResumeScore.ATS.job.dto.JobDescriptionRequest;
import com.ResumeScore.ATS.job.dto.JobDescriptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobDescriptionController {

    private final JobDescriptionService jobDescriptionService;

    public JobDescriptionController(JobDescriptionService jobDescriptionService) {
        this.jobDescriptionService = jobDescriptionService;
    }

    @PostMapping
    public ResponseEntity<
            ApiResponse<JobDescriptionResponse>
            >createDescription(
            @RequestBody JobDescriptionRequest request
            ){
        JobDescriptionResponse response=
                jobDescriptionService.createJobDescription(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job description created successfully",
                        response
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobDescriptionListResponse>>> getMyJobDescriptions() {
        List<JobDescriptionListResponse> response = jobDescriptionService.getMyJobDescriptions();
        return ResponseEntity.ok(
                ApiResponse.success("Job descriptions fetched successfully", response)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDescriptionResponse>> getJobDescriptionById(
            @PathVariable Long id
    ) {
        JobDescriptionResponse response = jobDescriptionService.getJobDescriptionById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Job description fetched successfully", response)
        );
    }
}
