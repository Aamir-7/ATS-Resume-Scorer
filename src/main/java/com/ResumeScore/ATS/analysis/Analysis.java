package com.ResumeScore.ATS.analysis;

import com.ResumeScore.ATS.job.JobDescription;
import com.ResumeScore.ATS.resume.Resume;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "analyses")
@Entity
@Getter
@Setter
public class Analysis {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @ManyToOne
    @JoinColumn(name = "job_description_id")
    private JobDescription jobDescription;

    private AnalysisStatus status;

    private double matchScore;

    private List<String> matchedKeyWords;

    private List<String>missingKeywords;

    private String Suggestions;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
