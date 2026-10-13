package org.example.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class BuildAnalysisService {

    private final AIBuildAnalysisService aiService;

    public BuildAnalysisService(AIBuildAnalysisService aiService) {
        this.aiService = aiService;
    }

    private static final Pattern BUILD_FAILURE_PAT = Pattern.compile("(?i)BUILD FAILURE");
    private static final Pattern ERROR_TAG_PAT = Pattern.compile("\\[ERROR\\]");
    private static final Pattern EXCEPTION_PAT = Pattern.compile("(?i)\\bException\\b");


    /**
     * Simple heuristic-based analysis for the PoC.
     * Scans the provided build log for common failure indicators and returns a basic analysis.
     */
    /**
     * Analyze the provided full build log by first extracting a relevant error snippet
     * and then delegating to the AI analysis service. Returns the AI response as a JSON string.
     *
     * @param buildLog full build log
     * @return AI analysis as JSON string
     */
    public org.example.dto.BuildAnalysisResponse analyze(String buildLog) {
        // extract relevant error portion (defensive)
        String snippet = extractRelevantError(buildLog);

        // delegate to AI service which handles null/empty input gracefully
        return aiService.analyzeWithAI(snippet);
    }

    /**
     * Simple echo method for PoC: returns the raw build log as-is (or empty string if null).
     * Kept separate so the controller/service separation remains clear and future logic
     * (pre-processing, normalization, or AI calls) can be added here.
     */
    public String echo(String buildLog) {
        return buildLog == null ? "" : buildLog;
    }

    /**
     * Extracts a concise, relevant error snippet from a full build log.
     * The method looks for well-known failure signatures (e.g. "BUILD FAILURE", "[ERROR]", "Exception")
     * and returns a focused block around the first detected occurrence. The returned snippet is trimmed.
     *
     * This method is defensive: it handles null/empty input and uses simple, extensible heuristics
     * to capture stack traces and related context lines.
     *
     * @param buildLog full build log text
     * @return a concise error snippet or an empty string when no error portion is found
     */
    public String extractRelevantError(String buildLog) {
        if (buildLog == null || buildLog.isBlank()) {
            return "";
        }

        String[] lines = buildLog.split("\\R");
        int n = lines.length;

        List<Pattern> patterns = List.of(BUILD_FAILURE_PAT, ERROR_TAG_PAT, EXCEPTION_PAT);

        // find the first line matching any error pattern
        int startIdx = -1;
        for (int i = 0; i < n; i++) {
            String l = lines[i];
            for (Pattern p : patterns) {
                if (p.matcher(l).find()) {
                    startIdx = i;
                    break;
                }
            }
            if (startIdx != -1) break;
        }

        if (startIdx == -1) {
            return ""; // no known error pattern found
        }

        // include some context lines before the error
        int contextBefore = 3;
        int s = Math.max(0, startIdx - contextBefore);

        // collect lines until we reach a clear break (blank line) or a likely new log heading
        int end = startIdx;
        int maxLines = 200; // safety cap
        for (int i = startIdx + 1; i < n && i - startIdx < maxLines; i++) {
            String l = lines[i];
            if (l.isBlank()) {
                end = i;
                break;
            }

            String trimmed = l.trim();
            boolean looksLikeStack = trimmed.startsWith("at ") || trimmed.startsWith("Caused by:") || l.startsWith("\t") || trimmed.startsWith("...");
            boolean containsErrorMarker = ERROR_TAG_PAT.matcher(l).find() || EXCEPTION_PAT.matcher(l).find() || BUILD_FAILURE_PAT.matcher(l).find();

            if (looksLikeStack || containsErrorMarker) {
                end = i;
                continue;
            }

            // Heuristic: if the next line starts without indentation and does not contain exception markers,
            // it's likely a new log entry; stop here to avoid returning the entire log.
            boolean startsWithNonWhitespace = !Character.isWhitespace(l.charAt(0));
            if (startsWithNonWhitespace) {
                break;
            } else {
                end = i;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = s; i <= end && i < n; i++) {
            sb.append(lines[i]).append(System.lineSeparator());
        }

        return sb.toString().trim();
    }

}
