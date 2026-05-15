package com.ResumeScore.ATS.job.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JobDescriptionResponse {

    private long id;
    private String title;
    private String companyName;
    private String rawText;

}
