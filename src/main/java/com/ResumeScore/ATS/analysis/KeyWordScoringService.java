package com.ResumeScore.ATS.analysis;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Getter
public class KeyWordScoringService {
    private static final Set<String>STOP_WORDS=Set.of(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
            "in", "is", "it", "of", "on", "or", "that", "the", "to", "with",
            "will", "this", "you", "your", "we", "our"
    );

    //nested class
    public static class AnalysisResult{
        private final double matchScore;
        private final List<String>matchedKeywords;
        private final List<String>missingKeywords;

        public AnalysisResult(double matchScore, List<String> matchedKeywords, List<String> missingKeywords) {
            this.matchScore = matchScore;
            this.matchedKeywords = matchedKeywords;
            this.missingKeywords = missingKeywords;
        }
    }

}


