package com.ResumeScore.ATS.job;

import com.ResumeScore.ATS.job.dto.JobDescriptionListResponse;
import com.ResumeScore.ATS.job.dto.JobDescriptionRequest;
import com.ResumeScore.ATS.job.dto.JobDescriptionResponse;
import com.ResumeScore.ATS.user.User;
import com.ResumeScore.ATS.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobDescriptionService {

    private final JobDescriptionRepository jobDescriptionRepository;
    private final UserRepository userRepository;

    public JobDescriptionService(JobDescriptionRepository jobDescriptionRepository, UserRepository userRepository) {
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.userRepository = userRepository;
    }

    public JobDescriptionResponse createJobDescription(JobDescriptionRequest request) {
        validateRequest(request);
        User currentUser = getCurrentUser();

        JobDescription jobDescription=new JobDescription();
        jobDescription.setTitle(request.getTitle());
        jobDescription.setCompanyName(request.getCompanyName());
        jobDescription.setRawText(request.getRawText());
        jobDescription.setUser(currentUser);

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

    private User getCurrentUser() {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        long userId = Long.parseLong(principal);
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<JobDescriptionListResponse> getMyJobDescriptions() {
        User currentUser = getCurrentUser();
        return jobDescriptionRepository.findAllByUserId(currentUser.getId())
                .stream()
                .map(jobDesc -> new JobDescriptionListResponse(
                        jobDesc.getId(),
                        jobDesc.getTitle(),
                        jobDesc.getCompanyName(),
                        jobDesc.getCreatedAt()
                ))
                .toList();
    }

    public JobDescriptionResponse getJobDescriptionById(Long id) {
        User currentUser = getCurrentUser();
        JobDescription jobDescription = jobDescriptionRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Job description not found"));

        return new JobDescriptionResponse(
                jobDescription.getId(),
                jobDescription.getTitle(),
                jobDescription.getCompanyName(),
                jobDescription.getRawText()
        );
    }
}
