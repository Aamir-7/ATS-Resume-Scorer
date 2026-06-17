package com.ResumeScore.ATS.analysis;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis,Long> {
    Optional<Analysis> findByIdAndUserId(Long id, Long userId);

    List<Analysis>findAllByUserId(Long userId);
    Page<Analysis>findAllByUserId(Long userId, Pageable pageable);

    Long countByUserId(Long userId);

    Optional<Analysis>findTopByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("select avg(a.matchScore) from Analysis a where a.user.id = :userId")
    Double findAverageMatchScoreByUserId(Long userId);

    @Query("select max(a.matchScore) from Analysis a where a.user.id = :userId")
    Double findBestScoreByUserId(Long userId);
}
