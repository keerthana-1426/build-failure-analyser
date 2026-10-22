package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
@Service
public class BuildAnalysisService {

    private final AiBuildAnalysisService aiBuildAnalysisService;

    public BuildAnalysisService(AiBuildAnalysisService aiBuildAnalysisService) {
        this.aiBuildAnalysisService = aiBuildAnalysisService;
    }

    /**
     * Delegates analysis to the AI service.
     *
     * @param snippet build error snippet
     * @param context surrounding context
     * @return JSON string with rootCause, errorType, suggestedFix
     * @throws JsonProcessingException if serialization fails
     */
    public String analyzeWithAI(String snippet, String context) throws JsonProcessingException {
        return aiBuildAnalysisService.analyzeError(snippet, context);
    }
}
