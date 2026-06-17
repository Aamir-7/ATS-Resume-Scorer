package com.ResumeScore.ATS.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report,Long> {
    Optional<Report> findByAnalysisIdAndUserId(Long analysisId, Long userId);
    Optional<Report> findByIdAndUserId(Long id, Long userId);
    List<Report> findAllByUserId(Long userId);
    Page<Report> findAllByUserId(Long userId, Pageable pageable);
    Long countByUserId(Long userId);
}
