package com.ResumeScore.ATS.resume;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="resumes")
public class Resume {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false,unique = true)
    private String storedFileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private String storagePath;

    @Lob
    @Column(columnDefinition = "TEXT")
    private  String extractedText;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate(){
        LocalDateTime now=LocalDateTime.now();
        this.createdAt=now;
        this.updatedAt=now;
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt=LocalDateTime.now();
    }
}
