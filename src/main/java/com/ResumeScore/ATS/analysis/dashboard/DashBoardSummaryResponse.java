package com.ResumeScore.ATS.analysis.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashBoardSummaryResponse {

    private long totalResumes;
    private long totalJobDescriptions;
    private long totalAnalyses;
    private long totalReports;
    private double averageMatchScore;
    private double bestMatchScore;
    private Long latestAnalysisId;

}
