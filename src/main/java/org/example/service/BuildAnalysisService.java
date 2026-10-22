package org.example.service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.example.dto.BuildAnalysisResponse;
import org.springframework.stereotype.Service;

@Service
public class BuildAnalysisService {

    private static final Pattern BUILD_FAILURE_PAT = Pattern.compile("(?i)BUILD FAILURE");
    private static final Pattern ERROR_TAG_PAT = Pattern.compile("\\[ERROR\\]");
    private static final Pattern EXCEPTION_PAT = Pattern.compile("(?i)\\bException\\b");
    private static final int CONTEXT_BEFORE = 3;
    private static final int MAX_SNIPPET_LINES = 200;
    private static final List<Pattern> ERROR_PATTERNS = List.of(BUILD_FAILURE_PAT, ERROR_TAG_PAT, EXCEPTION_PAT);

    private final AiBuildAnalysisService aiService;

    public BuildAnalysisService(AiBuildAnalysisService aiService) {
        this.aiService = aiService;
    }

    public BuildAnalysisResponse analyze(String buildLog) {
        String snippet = extractRelevantError(buildLog);
        return aiService.analyzeWithAI(snippet);
    }

    public String echo(String buildLog) {
        return buildLog == null ? "" : buildLog;
    }

    public String extractRelevantError(String buildLog) {
        if (buildLog == null || buildLog.isBlank()) {
            return "";
        }

        String[] lines = buildLog.split("\\R");
        int startIdx = findErrorStartIndex(lines);
        if (startIdx == -1) {
            return "";
        }

        int start = Math.max(0, startIdx - CONTEXT_BEFORE);
        int end = findEndIndex(lines, startIdx);
        return buildSnippet(lines, start, end);
    }

    private int findErrorStartIndex(String[] lines) {
        return IntStream.range(0, lines.length)
                .filter(index -> ERROR_PATTERNS.stream().anyMatch(pattern -> pattern.matcher(lines[index]).find()))
                .findFirst()
                .orElse(-1);
    }

    private String buildSnippet(String[] lines, int start, int end) {
        StringBuilder snippet = new StringBuilder();
        for (int index = start; index <= end && index < lines.length; index++) {
            snippet.append(lines[index]).append(System.lineSeparator());
        }
        return snippet.toString().trim();
    }

    private int findEndIndex(String[] lines, int startIdx) {
        int end = startIdx;
        for (int index = startIdx + 1; index < lines.length && index - startIdx < MAX_SNIPPET_LINES; index++) {
            String line = lines[index];
            if (line.isBlank()) {
                return index;
            }
            if (looksLikeStackTrace(line) || containsErrorMarker(line)) {
                end = index;
                continue;
            }
            if (!Character.isWhitespace(line.charAt(0))) {
                break;
            }
            end = index;
        }
        return end;
    }

    private boolean looksLikeStackTrace(String line) {
        String trimmed = line.trim();
        return trimmed.startsWith("at ")
                || trimmed.startsWith("Caused by:")
                || line.startsWith("\t")
                || trimmed.startsWith("...");
    }

    private boolean containsErrorMarker(String line) {
        return ERROR_TAG_PAT.matcher(line).find()
                || EXCEPTION_PAT.matcher(line).find()
                || BUILD_FAILURE_PAT.matcher(line).find();
    }
}
