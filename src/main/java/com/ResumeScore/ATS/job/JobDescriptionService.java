package com.ResumeScore.ATS.job;

import com.ResumeScore.ATS.job.dto.JobDescriptionRequest;
import com.ResumeScore.ATS.job.dto.JobDescriptionResponse;
import org.springframework.stereotype.Service;

@Service
public class JobDescriptionService {

    private final JobDescriptionRepository jobDescriptionRepository;

    public JobDescriptionService(JobDescriptionRepository jobDescriptionRepository) {
        this.jobDescriptionRepository = jobDescriptionRepository;
    }

    public JobDescriptionResponse createJobDescription(JobDescriptionRequest request) {
        validateRequest(request);

        JobDescription jobDescription=new JobDescription();
        jobDescription.setTitle(request.getTitle());
        jobDescription.setCompanyName(request.getCompanyName());
        jobDescription.setRawText(request.getRawText());

        JobDescription savedJob=
                jobDescriptionRepository.save(jobDescription);

        return new JobDescriptionResponse(
                savedJob.getId(),
                savedJob.getTitle(),
                savedJob.getCompanyName(),
                savedJob.getRawText()
        );
    }

    private void validateRequest(JobDescriptionRequest request){

        if (request==null){
            throw new IllegalArgumentException(
                    "Request can not be null "
            );
        }

        if (request.getTitle()==null ||
                request.getTitle().trim().isBlank()){
            throw new IllegalArgumentException(
                    "The title can not ne empty "
            );
        }

        if (request.getCompanyName()==null ||
                request.getCompanyName().trim().isBlank()){
            throw new IllegalArgumentException(
                    "Company name can not be null "
            );
        }

        if (request.getRawText()==null ||
                request.getRawText().trim().isBlank()){
            throw new IllegalArgumentException(
                    "Job description can not be empty "
            );

        }
    }
}
