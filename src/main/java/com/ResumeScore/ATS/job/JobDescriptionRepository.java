package com.ResumeScore.ATS.job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobDescriptionRepository extends JpaRepository<JobDescription,Long> {
    Optional<JobDescription> findByIdAndUserId(Long id, Long userId);
}
