package com.ResumeScore.ATS.job;

import com.ResumeScore.ATS.common.ApiResponse;
import com.ResumeScore.ATS.common.PageResponse;
import com.ResumeScore.ATS.job.dto.JobDescriptionListResponse;
import com.ResumeScore.ATS.job.dto.JobDescriptionRequest;
import com.ResumeScore.ATS.job.dto.JobDescriptionResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<ApiResponse<PageResponse<JobDescriptionListResponse>>>
    getMyJobDescriptions(
            @PageableDefault(size = 10, sort = "createdAt", direction =
                    Sort.Direction.DESC)
            Pageable pageable
    ) {
        PageResponse<JobDescriptionListResponse> response =
                jobDescriptionService.getMyJobDescriptions(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Job descriptions fetched successfully",
                        response)
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDescriptionResponse>>updateDesc(
            @PathVariable Long id,
            @RequestBody JobDescriptionRequest request
    ){
        JobDescriptionResponse response=jobDescriptionService.updateDesc(id,request);
        return ResponseEntity.ok(
                ApiResponse.success("Job desc updated successfully ",response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable Long id
    ){
        jobDescriptionService.deleteJobDesc(id);
        return ResponseEntity.ok(ApiResponse.success("Job description deleted successfully ",null));
    }
}
