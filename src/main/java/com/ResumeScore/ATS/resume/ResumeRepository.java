package com.ResumeScore.ATS.resume;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume,Long> {
    Optional<Resume> findByIdAndUserId(Long id, Long userId);
}
