package com.ResumeScore.ATS.job;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobDescriptionRepository extends JpaRepository<JobDescription,Long> {
    Optional<JobDescription> findByIdAndUserId(Long id, Long userId);
    List<JobDescription>findAllByUserId(Long userId);
    Page<JobDescription>findAllByUserId(Long userId, Pageable pageable);
    Long countByUserId(Long userId);
}
