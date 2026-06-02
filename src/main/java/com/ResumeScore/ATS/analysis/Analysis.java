
package com.ResumeScore.ATS.analysis;

import com.ResumeScore.ATS.job.JobDescription;
import com.ResumeScore.ATS.resume.Resume;
import com.ResumeScore.ATS.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "analyses")
@Getter
@Setter
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_description_id")
    private JobDescription jobDescription;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status;

    @Column(nullable = false)
    private double matchScore;

    @ElementCollection
    @CollectionTable(name = "analysis_matched_keywords", joinColumns =
    @JoinColumn(name = "analysis_id"))
    @Column(name = "keyword")
    private List<String> matchedKeywords = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "analysis_missing_keywords", joinColumns =
    @JoinColumn(name = "analysis_id"))
    @Column(name = "keyword")
    private List<String> missingKeywords = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT")
    private String Summary;

    @ElementCollection
    @CollectionTable(name = "analysis_strengths",joinColumns = @JoinColumn(name =
    "analysis_id"))
    @Column(name = "value")
    private List<String> strengths=new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "analysis_weaknesses",joinColumns = @JoinColumn(name =
    "analysis_id"))
    @Column(name = "value")
    private List<String>weaknesses=new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "analysis_ats_risks",joinColumns = @JoinColumn(name =
    "analysis_id"))
    @Column(name = "value")
    private List<String> atsRisks=new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "analysis_rewrite_suggestions",joinColumns =
    @JoinColumn(name = "analysis_id"))
    @Column(name = "value")
    private List<String> rewriteSuggestions=new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "analysis_improved_bullets",joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "value")
    private List<String>improvedBullets=new ArrayList<>();

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
