package com.ResumeScore.ATS.resume;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume,Long> {
    Optional<Resume> findByIdAndUserId(Long id, Long userId);
    List<Resume>findAllByUserId(Long userId);
    Long countByUserId(Long userId);
    Page<Resume>findAllByUserId(Long userId, Pageable pageable);
}
