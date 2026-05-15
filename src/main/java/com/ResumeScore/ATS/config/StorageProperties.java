package com.ResumeScore.ATS.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    private String resumeDir;

    public String getResumeDir() {
        return resumeDir;
    }

    public void setResumeDir(String resumeDir) {
        this.resumeDir = resumeDir;
    }
}