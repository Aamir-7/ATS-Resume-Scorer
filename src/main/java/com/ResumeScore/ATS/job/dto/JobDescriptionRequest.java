package com.ResumeScore.ATS.job.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobDescriptionRequest {

    private String title;
    private String companyName;
    private String rawText;
}
