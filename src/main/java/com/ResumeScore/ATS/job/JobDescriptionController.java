package com.ResumeScore.ATS.job;

import com.ResumeScore.ATS.common.ApiResponse;
import com.ResumeScore.ATS.job.dto.JobDescriptionRequest;
import com.ResumeScore.ATS.job.dto.JobDescriptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
