package com.ResumeScore.ATS.analysis;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class KeyWordScoringService {

    private static final Set<String> SKILL_PHRASES = Set.of(
            "java",
            "spring boot",
            "spring",
            "hibernate",
            "microservices",
            "rest api",
            "rest",
            "postgresql",
            "mysql",
            "oracle",
            "mongodb",
            "docker",
            "kubernetes",
            "aws",
            "amazon web services",
            "git",
            "github",
            "maven",
            "gradle",
            "react",
            "javascript",
            "typescript",
            "html",
            "css",
            "python",
            "sql"
    );

    public AnalysisResult analyze(String resumeText, String jobDescriptionText) {
        validateText(resumeText, jobDescriptionText);

        String normalizedResume = normalizeText(resumeText);
        String normalizedJob = normalizeText(jobDescriptionText);

        Set<String> resumeKeywords = extractSkillKeywords(normalizedResume);
        Set<String> jobKeywords = extractSkillKeywords(normalizedJob);

        Set<String> matchedKeywords = new HashSet<>(jobKeywords);
        matchedKeywords.retainAll(resumeKeywords);

        Set<String> missingKeywords = new HashSet<>(jobKeywords);
        missingKeywords.removeAll(resumeKeywords);

        double matchScore = calculateScore(matchedKeywords.size(),
                jobKeywords.size());

        return new AnalysisResult(
                round(matchScore),
                toSortedList(matchedKeywords),
                toSortedList(missingKeywords)
        );
    }

    private void validateText(String resumeText, String jobDescriptionText) {
        if (resumeText == null || resumeText.isBlank()) {
            throw new IllegalArgumentException("Resume text can not be empty");
        }

        if (jobDescriptionText == null || jobDescriptionText.isBlank()) {
            throw new IllegalArgumentException("Job description text can not be empty");
        }
    }

    private String normalizeText(String text) {
        return text.toLowerCase()
                .replace("amazon web services", "aws")
                .replace("restful api", "rest api")
                .replace("restful apis", "rest api")
                .replace("postgres", "postgresql")
                .replaceAll("[^a-z0-9+#. ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private Set<String> extractSkillKeywords(String text) {
        Set<String> foundSkills = new HashSet<>();

        List<String> orderedPhrases = SKILL_PHRASES.stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .toList();

        for (String phrase : orderedPhrases) {
            if (containsWholePhrase(text, phrase) && !isCoveredByLongerPhrase(phrase, foundSkills)) {
                foundSkills.add(phrase);
            }
        }

        return foundSkills;
    }

    private boolean containsWholePhrase(String text, String phrase) {
        String wrappedText = " " + text + " ";
        String wrappedPhrase = " " + phrase + " ";
        return wrappedText.contains(wrappedPhrase);
    }

    private boolean isCoveredByLongerPhrase(String phrase, Set<String> foundSkills) {
        return foundSkills.stream().anyMatch(existing ->
                existing.contains(phrase) && !existing.equals(phrase));
    }

    private double calculateScore(int matchedCount, int totalJobKeywords) {
        if (totalJobKeywords == 0) {
            return 0.0;
        }
        return ((double) matchedCount / totalJobKeywords) * 100;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private List<String> toSortedList(Set<String> keywords) {
        return keywords.stream().sorted().collect(Collectors.toList());
    }

    public static class AnalysisResult {
        private final double matchScore;
        private final List<String> matchedKeywords;
        private final List<String> missingKeywords;

        public AnalysisResult(double matchScore, List<String> matchedKeywords,
                              List<String> missingKeywords) {
            this.matchScore = matchScore;
            this.matchedKeywords = matchedKeywords;
            this.missingKeywords = missingKeywords;
        }

        public double getMatchScore() {
            return matchScore;
        }

        public List<String> getMatchedKeywords() {
            return matchedKeywords;
        }

        public List<String> getMissingKeywords() {
            return missingKeywords;
        }
    }
}



