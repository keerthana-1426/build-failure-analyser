package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Simulated AI service for analyzing extracted build error snippets.
 */
@Service
public class AiBuildAnalysisService {   // ✅ matches file name
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyzeError(String snippet, String context) throws JsonProcessingException {
        Map<String, String> result = new HashMap<>();
        result.put("rootCause", "Simulated root cause");
        result.put("errorType", "Simulated error type");
        result.put("suggestedFix", "Simulated fix suggestion");
        return objectMapper.writeValueAsString(result);
    }
}
