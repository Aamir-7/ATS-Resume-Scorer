package com.ResumeScore.ATS.analysis.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;


@Component
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    private final GeminiConfig geminiConfig;
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiClient(GeminiConfig geminiConfig){
        this.geminiConfig = geminiConfig;
        validateConfig();
        this.restClient = RestClient.builder()
                .baseUrl(geminiConfig.getBaseUrl())
                .build();
        log.info("Gemini config loaded. baseUrl={}, model={}, apiKeyPresent={}",
                geminiConfig.getBaseUrl(),
                geminiConfig.getModel(),
                geminiConfig.getApiKey() != null && !geminiConfig.getApiKey().isBlank());
    }

    public String generateContent(String prompt) {
        Map<String, Object> requestBody =
                Map.of("contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text",prompt)
                        })
                });

        String rawResponse = restClient.post()
                .uri("/v1beta/models/" + geminiConfig.getModel() + ":generateContent")
                .header("x-goog-api-key", geminiConfig.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        log.info("Gemini raw response: {}", rawResponse);
        return extractText(rawResponse);
    }

    private String extractText(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return "";
        }

        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                return "";
            }

            JsonNode parts = candidates.get(0).path("content").path("parts");
            if (!parts.isArray() || parts.isEmpty()) {
                return "";
            }

            StringBuilder text = new StringBuilder();
            for (JsonNode part : parts) {
                String value = part.path("text").asText("");
                if (!value.isBlank()) {
                    if (!text.isEmpty()) {
                        text.append("\n");
                    }
                    text.append(value);
                }
            }

            return text.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    private void validateConfig() {
        if (geminiConfig.getApiKey() == null || geminiConfig.getApiKey().isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY is missing for the Spring application");
        }
        if (geminiConfig.getApiKey().contains("${")) {
            throw new IllegalStateException("GEMINI_API_KEY placeholder was not resolved. The Spring application is not receiving the environment variable.");
        }
        if (geminiConfig.getBaseUrl() == null || geminiConfig.getBaseUrl().isBlank()) {
            throw new IllegalStateException("Gemini base URL is missing");
        }
        if (geminiConfig.getModel() == null || geminiConfig.getModel().isBlank()) {
            throw new IllegalStateException("Gemini model is missing");
        }
    }
}
