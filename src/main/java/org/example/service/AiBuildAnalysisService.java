package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Simulated AI service for analyzing extracted build error snippets.
 *
 * <p>This class is a placeholder that builds a prompt, simulates calling an AI model,
 * and returns a structured JSON string containing rootCause, errorType and suggestedFix.
 * The implementation uses simple heuristics to generate the simulated response. In future
 * iterations, replace {@link #simulateAIResponse(String, String)} with a real API call
 * (HTTP client, authentication, retries, rate limiting, etc.).
 */
@Service
public class AiBuildAnalysisService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Analyze the extracted error log with an AI model (simulated).
     *
     * @param errorLog extracted error snippet (not the full build log)
     * @return structured analysis as a JSON string
     */
    public org.example.dto.BuildAnalysisResponse analyzeWithAI(String errorLog) {
        if (errorLog == null || errorLog.isBlank()) {
            return new org.example.dto.BuildAnalysisResponse("", "", "", "0.00", "No error log provided");
        }

        String prompt = buildPrompt(errorLog);
        Map<String, Object> response = simulateAIResponse(prompt, errorLog);

        String rootCause = (String) response.getOrDefault("rootCause", "");
        String errorType = (String) response.getOrDefault("errorType", "");
        String suggestedFix = (String) response.getOrDefault("suggestedFix", "");
        String confidence = (String) response.getOrDefault("confidence", "0.00");
        String note = "";
        if (response.containsKey("note")) {
            note = String.valueOf(response.get("note"));
        }

        return new org.example.dto.BuildAnalysisResponse(rootCause, errorType, suggestedFix, confidence, note);
    }

    /**
     * Build a descriptive prompt to send to the AI model. Keeps instructions explicit and focused.
     */
    private String buildPrompt(String errorLog) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert build failure analyst. Given the following error snippet,\n");
        sb.append("(1) identify the most likely root cause in one concise sentence.\n");
        sb.append("(2) classify the error type (dependency, compilation/code, runtime/resource, other).\n");
        sb.append("(3) suggest a short, actionable fix or mitigation.\n\n");
        sb.append("Error snippet:\n---\n");
        sb.append(errorLog.trim()).append("\n---\n");
        sb.append("Provide the answer as a JSON object with fields: rootCause, errorType, suggestedFix, confidence (0-1).\n");
        return sb.toString();
    }

    /**
     * Simulate an AI model's structured analysis using lightweight heuristics. Replace with real API call later.
     */
    private Map<String, Object> simulateAIResponse(String prompt, String errorLog) {
        Map<String, Object> out = new HashMap<>();
        String normalized = errorLog.toLowerCase(Locale.ROOT);

        String errorType = classifyErrorType(normalized);
        String rootCause = inferRootCause(normalized, errorType);
        String suggestedFix = suggestFix(rootCause, errorType);
        double confidence = estimateConfidence(normalized, errorType);

        out.put("rootCause", rootCause);
        out.put("errorType", errorType);
        out.put("suggestedFix", suggestedFix);
        out.put("confidence", String.format(Locale.ROOT, "%.2f", confidence));
        out.put("prompt", prompt);

        return out;
    }

    private String classifyErrorType(String normalizedLog) {
        if (normalizedLog.contains("could not find") || normalizedLog.contains("failed to resolve") || normalizedLog.contains("artifact not found") || normalizedLog.contains("repository")) {
            return "DEPENDENCY";
        }

        if (normalizedLog.contains("cannot find symbol") || normalizedLog.contains("compilation") || normalizedLog.contains("cannot find") || normalizedLog.contains("symbol:")) {
            return "CODE/COMPILATION";
        }

        if (normalizedLog.contains("outofmemory") || normalizedLog.contains("out of memory") || normalizedLog.contains("java.lang.outofmemoryerror")) {
            return "RESOURCE";
        }

        if (normalizedLog.contains("exception") || normalizedLog.contains("stacktrace") || normalizedLog.contains("at ")) {
            return "RUNTIME";
        }

        return "OTHER";
    }

    private String inferRootCause(String normalizedLog, String errorType) {
        // Basic heuristics to craft a one-line root cause explanation.
        if ("DEPENDENCY".equals(errorType)) {
            return "Dependency resolution failed: a required artifact could not be found in configured repositories.";
        }

        if ("CODE/COMPILATION".equals(errorType)) {
            return "Compilation error: source code references missing symbols or has type errors.";
        }

        if ("RESOURCE".equals(errorType)) {
            return "Resource exhaustion: the JVM ran out of memory during the build.";
        }

        if ("RUNTIME".equals(errorType)) {
            // try to extract the exception message line if present
            String[] lines = normalizedLog.split("\\R");
            for (String l : lines) {
                if (l.contains("exception") || l.contains("caused by:")) {
                    return l.trim();
                }
            }
            return "Runtime exception or error occurred during the build.";
        }

        // fallback: return a short excerpt of the log
        String excerpt = normalizedLog.length() > 200 ? normalizedLog.substring(0, 200) + "..." : normalizedLog;
        return "Unclassified error: " + excerpt;
    }

    private String suggestFix(String rootCause, String errorType) {
        switch (errorType) {
            case "DEPENDENCY":
                return "Check repository configuration, ensure the artifact coordinates are correct, and verify network access to repositories. Consider adding a mirror or uploading the missing artifact.";
            case "CODE/COMPILATION":
                return "Inspect the compilation errors, fix missing imports or types, and run a local `mvn -DskipTests compile` to reproduce. Consider reverting recent code changes that introduced the symbol mismatch.";
            case "RESOURCE":
                return "Increase the JVM heap for the build (e.g., MAVEN_OPTS='-Xmx2g') or reduce parallelism. Investigate memory-hungry plugins/tests.";
            case "RUNTIME":
                return "Inspect the stack trace to find the exact failing class/method, add null checks or handle expected exceptions. Re-run the failing step with debug logging enabled.";
            default:
                return "Inspect the provided snippet and surrounding build log for more clues. If intermittent, re-run the build to collect a full trace.";
        }
    }

    private double estimateConfidence(String normalizedLog, String errorType) {
        double base = 0.6;
        switch (errorType) {
            case "DEPENDENCY":
            case "CODE/COMPILATION":
            case "RESOURCE":
                base = 0.85;
                break;
            case "RUNTIME":
                base = 0.75;
                break;
            default:
                base = 0.5;
        }

        // boost confidence if specific keywords appear
        if (normalizedLog.contains("cannot find") || normalizedLog.contains("outofmemory") || normalizedLog.contains("could not find")) {
            base = Math.min(0.99, base + 0.1);
        }
        return base;
    }

    private String emptyResultJson(String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("rootCause", "");
        m.put("errorType", "");
        m.put("suggestedFix", "");
        m.put("confidence", "0.00");
        m.put("note", message);
        try {
            return objectMapper.writeValueAsString(m);
        } catch (JsonProcessingException e) {
            return "{\"note\":\"" + message + "\"}";
        }
    }
}

